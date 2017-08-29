package kr.dja.plciot.Device.Connection.PacketReceive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import kr.dja.plciot.PLC_IoT_Core;
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
	
	public static class UDPRawSocketThreadManage extends Thread
	{// UDPRawSocketReceiver 클래스를 빌드하고 작업을 실행합니다.
		
		private UDPRawSocketReceiver instance;
		private TaskLock startLock;
		private TaskLock shutdownLock;
		
		public UDPRawSocketThreadManage(int port, IRawSocketObserver receiveManager, TaskLock startLock)
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
			PLC_IoT_Core.CONS.push("장치 수신자 포트 " + this.instance.getLocalPort() + " 번 활성화.");
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
