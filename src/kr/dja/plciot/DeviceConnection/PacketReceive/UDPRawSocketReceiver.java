package kr.dja.plciot.DeviceConnection.PacketReceive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.DeviceConnection.PacketProcess;
import kr.dja.plciot.Task.TaskLock;

public class UDPRawSocketReceiver
{
	private final DatagramSocket socket;
	private final IRawSocketObserver receiveManager;
	private boolean threadFlag;
	
	private UDPRawSocketReceiver(DatagramSocket socket, IRawSocketObserver receiveManager)
	{
		this.socket = socket;
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
				this.socket.receive(packet);
				this.receiveManager.rawPacketResive(packet.getPort(), packet.getAddress(), buffer);
				packet.getAddress();
			}
			catch (IOException e)
			{
			}
		}
	}
	
	public static class UDPRawSocketThreadManage extends Thread
	{// UDPRawSocketReceiver Ŭ������ �����ϰ� �۾��� �����մϴ�.
		
		private final UDPRawSocketReceiver instance;
		private TaskLock startLock;
		private TaskLock shutdownLock;
		
		public UDPRawSocketThreadManage(DatagramSocket socket, IRawSocketObserver receiveManager, TaskLock startLock)
		{
			this.instance = new UDPRawSocketReceiver(socket, receiveManager);
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
			PLC_IoT_Core.CONS.push("��ġ ������ " + this.instance.socket.getLocalPort() + " �� Ȱ��ȭ.");
			this.startLock.unlock();
			this.instance.executeTask();
			
			PLC_IoT_Core.CONS.push("��ġ ������ ��Ȱ��ȭ.");
			this.shutdownLock.unlock();
		}
		
		public void stopTask(TaskLock shutdownLock)
		{
			this.shutdownLock = shutdownLock;
			this.instance.threadFlag = false;
		}
	}
}