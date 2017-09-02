package kr.dja.plciot.Device.Connection;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.Connection.PacketReceive.ReceiveController;
import kr.dja.plciot.Device.Connection.PacketReceive.ReceiveController.ReceiveControllerBuildManager;
import kr.dja.plciot.Device.Connection.PacketSend.SendController;
import kr.dja.plciot.Device.Connection.PacketSend.UDPRawSocketSender;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class ConnectionManager
{
	private static final int UDP_RCV_PORT_START = 50000;
	private static final int UDP_RCV_PORT_END = 50010;
	private static final int UDP_SND_PORT_START = 50011;
	private static final int UDP_SND_PORT_END = 50020;
	
	// 디바이스 통신모듈.
	private ReceiveController receiveController;
	private SendController sendController;
	
	public ConnectionManager()
	{
		
	}
	
	public static class ConnectionManagerBuilder implements IMultiThreadTaskCallback
	{
		private final ConnectionManager instance;
		private ReceiveControllerBuildManager rcvBuildManager;
		private IReceiveRegister receiveRegister;
		
		public ConnectionManagerBuilder(ConnectionManager instance)
		{
			this.instance = instance;
		}

		@Override
		public void executeTask(TaskOption option, NextTask nextTask)
		{// 디비접속해서 장치목록긁어오기
			if(option == TaskOption.START)
			{
				this.createInstance(nextTask);
			}
			else if(option == TaskOption.SHUTDOWN)
			{
				this.disposInstance(nextTask);
			}
		}
		
		public void setReceiveRegister(IReceiveRegister receiveRegister)
		{
			this.receiveRegister = receiveRegister;
		}
		
		private void createInstance(NextTask nextTask)
		{
			PLC_IoT_Core.CONS.push("장치 통신 관리자 로드 시작.");
			
			ReceiveController receiveController = new ReceiveController(this.receiveRegister);
			this.rcvBuildManager = new ReceiveControllerBuildManager(receiveController);
			SendController sendController = new SendController(this.createSocketList(UDP_SND_PORT_START, UDP_SND_PORT_END));
			this.rcvBuildManager.setDatagramSocket(this.createSocketList(UDP_RCV_PORT_START, UDP_RCV_PORT_END));
			
			this.instance.receiveController = receiveController;
			this.instance.sendController = sendController;
			
			nextTask.insertTask((TaskOption option, NextTask startNext)->
			{
				PLC_IoT_Core.CONS.push("장치 통신 관리자 활성화.");
				
				String testPacketUID = PacketProcess.CreateUUID("001F1F1F1FAA");
				
				receiveController.addObserver(testPacketUID, (byte[] packet)->
				{
					PLC_IoT_Core.CONS.push("장치 통신 테스트 완료.");
					PLC_IoT_Core.CONS.push(PacketProcess.GetPacketName(packet));
					startNext.nextTask();
				});
				
				byte[] testPacket = PacketProcess.CreateDataSet();
				PacketProcess.InputPacketHeader(testPacket, testPacketUID, PacketProcess.PHASE_CHECK);
				try
				{
					sendController.sendData(InetAddress.getByName("127.0.0.1"), testPacket);
				}
				catch(Exception e){};
			});
			
			nextTask.insertTask(this.rcvBuildManager);
			
			nextTask.nextTask();
		}
		
		private void disposInstance(NextTask nextTask)
		{
			nextTask.insertTask(this.rcvBuildManager);
		}
		
		private List<DatagramSocket> createSocketList(int portStart, int portEnd)
		{
			List<DatagramSocket> socketList = new ArrayList<DatagramSocket>();
			for(int i = portStart; i <= portEnd; ++i)
			{
				try
				{
					socketList.add(new DatagramSocket(i));
				}
				catch (SocketException e)
				{
					e.printStackTrace();
				}
			}
			return socketList;
		}
		
	}
}
