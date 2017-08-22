package kr.dja.plciot.Device.Connection.PacketReceive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import kr.dja.plciot.Device.Connection.PacketProcess;

public class UDPPortReceiver extends DatagramSocket implements Runnable
{
	private final IPacketReceiver receiveManager;
	
	UDPPortReceiver(int port, IPacketReceiver receiveManager) throws SocketException
	{
		super(port);

		this.receiveManager = receiveManager;
	}

	@Override
	public void run()
	{
		while(true)
		{
			byte[] buffer = PacketProcess.CreateDataSet();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try
			{
				this.receive(packet);
				this.receiveManager.PacketResive(packet.getPort(), packet.getAddress(), buffer);
				packet.getAddress();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
