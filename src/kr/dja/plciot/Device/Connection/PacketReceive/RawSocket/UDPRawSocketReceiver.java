package kr.dja.plciot.Device.Connection.PacketReceive.RawSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import kr.dja.plciot.Device.Connection.PacketProcess;

public class UDPRawSocketReceiver extends DatagramSocket implements Runnable
{
	private final IRawSocketObserver receiveManager;
	private boolean threadFlag;
	
	public UDPRawSocketReceiver(int port, IRawSocketObserver receiveManager) throws SocketException
	{
		super(port);

		this.receiveManager = receiveManager;
		this.threadFlag = true;
	}

	@Override
	public void run()
	{
		while(this.threadFlag)
		{
			byte[] buffer = PacketProcess.CreateDataSet();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try
			{
				this.receive(packet);
				this.receiveManager.rawPacketResive(packet.getPort(), packet.getAddress(), buffer);
				packet.getAddress();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void stop()
	{
		this.threadFlag = false;
		this.close();
	}
}
