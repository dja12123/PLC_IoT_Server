package kr.dja.plciot.Device.Connection.PacketReceive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import kr.dja.plciot.Device.Connection.PacketProcess;
import kr.dja.plciot.Task.TaskLock;

public class UDPRawSocketReceiver extends DatagramSocket
{
	private final IRawSocketObserver receiveManager;
	private boolean threadFlag;
	
	private UDPRawSocketReceiver(int port, IRawSocketObserver receiveManager) throws SocketException
	{
		super(port);

		this.receiveManager = receiveManager;
		this.threadFlag = true;
	}

	private void executeTask()
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
	
	public static class UDPRawSocketThread extends Thread
	{	
		private UDPRawSocketReceiver instance;
		private TaskLock startLock;
		private TaskLock shutdownLock;
		
		public UDPRawSocketThread(int port, IRawSocketObserver receiveManager, TaskLock startLock)
		{
			try
			{
				this.instance = new UDPRawSocketReceiver(port, receiveManager);
			}
			catch (SocketException e)
			{
				e.printStackTrace();
			}
			this.startLock = startLock;
			
			this.start();
		}
		
		public UDPRawSocketReceiver getInstance()
		{
			return this.instance;
		}
		
		@Override
		public void run()
		{
			this.startLock.unlock();
			this.instance.executeTask();
			this.shutdownLock.unlock();
		}
		
		public void stopTask(TaskLock shutdownLock)
		{
			this.shutdownLock = shutdownLock;
			this.instance.threadFlag = false;
			this.instance.close();
		}
	}
}
