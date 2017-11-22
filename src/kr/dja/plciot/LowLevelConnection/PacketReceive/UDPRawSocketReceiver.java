package kr.dja.plciot.LowLevelConnection.PacketReceive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.LowLevelConnection.PacketProcess;
import kr.dja.plciot.Task.TaskLock;

public class UDPRawSocketReceiver
{
	private final DatagramSocket socket;
	private final IRawSocketObserver receiveManager;
	private boolean threadFlag;
	private final byte[] buffer;
	private final DatagramPacket packet;
	
	private UDPRawSocketReceiver(DatagramSocket socket, IRawSocketObserver receiveManager)
	{
		this.socket = socket;
		this.receiveManager = receiveManager;
		this.threadFlag = true;
		this.buffer = new byte[PacketProcess.MAX_PACKET_LENGTH];
		this.packet = new DatagramPacket(this.buffer, this.buffer.length);
	}

	private void executeTask()
	{
		while(this.threadFlag)
		{
			try
			{
				this.socket.receive(this.packet);
				byte[] copyArr = new byte[PacketProcess.GetPacketSize(this.buffer)];
				System.arraycopy(this.buffer, 0, copyArr, 0, copyArr.length);
				this.receiveManager.rawPacketResive(this.packet.getPort(), this.packet.getAddress(), copyArr);
			}
			catch (IOException e)
			{
				//e.printStackTrace();
			}
		}
	}
	
	public static class UDPRawSocketThreadManage extends Thread
	{// UDPRawSocketReceiver 클래스를 빌드하고 작업을 실행합니다.
		
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
			PLC_IoT_Core.CONS.push("로우 레벨 수신자 " + this.instance.socket.getLocalPort() + " 번 활성화.");
			this.startLock.unlock();
			this.instance.executeTask();
			
			PLC_IoT_Core.CONS.push("로우 레벨 수신자 비활성화.");
			this.shutdownLock.unlock();
		}
		
		public void stopTask(TaskLock shutdownLock)
		{
			this.shutdownLock = shutdownLock;
			this.instance.threadFlag = false;
		}
	}
}
