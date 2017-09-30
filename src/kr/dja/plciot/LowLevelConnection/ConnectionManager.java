package kr.dja.plciot.LowLevelConnection;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.LowLevelConnection.Cycle.AbsCycle;
import kr.dja.plciot.LowLevelConnection.Cycle.IEndCycleCallback;
import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;
import kr.dja.plciot.LowLevelConnection.Cycle.ReceiveCycle.ReceiveCycleBuilder;
import kr.dja.plciot.LowLevelConnection.Cycle.SendCycle.SendCycleBuilder;
import kr.dja.plciot.LowLevelConnection.PacketReceive.IFirstReceiveObserver;
import kr.dja.plciot.LowLevelConnection.PacketReceive.ReceiveController;
import kr.dja.plciot.LowLevelConnection.PacketReceive.ReceiveController.ReceiveControllerBuildManager;
import kr.dja.plciot.LowLevelConnection.PacketSend.SendController;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class ConnectionManager implements IFirstReceiveObserver, IEndCycleCallback
{
	private static final int UDP_RCV_PORT_START = 50000;
	private static final int UDP_RCV_PORT_END = 50010;
	private static final int UDP_SND_PORT_START = 50011;
	private static final int UDP_SND_PORT_END = 50020;
	
	// 디바이스 통신모듈.
	private ReceiveController receiveController;
	private SendController sendController;
	
	private final List<IReceiveHandler> receiveHandlers;
	
	private final List<AbsCycle> cycles;

	public ConnectionManager()
	{
		this.receiveHandlers = Collections.synchronizedList(new ArrayList<IReceiveHandler>());
		
		this.cycles = Collections.synchronizedList(new ArrayList<AbsCycle>());
	}
	
	public void addReceiveHandler(IReceiveHandler handler)
	{
		this.receiveHandlers.add(handler);
	}
	
	public void removeReceiveHandler(IReceiveHandler handler)
	{
		this.receiveHandlers.remove(handler);
	}
	
	public void startSendCycle(InetAddress addr, int port
			,String macAddr, String name, String data, IPacketCycleUser target)
	{
		AbsCycle sendCycle = new SendCycleBuilder()
				.setPacketName(name)
				.setPacketData(data)
				.setEndCycleCallback(this)
				.setSender(this.sendController)
				.setReceiver(this.receiveController)
				.setPacketFullUID(PacketProcess.CreateFULLUID(macAddr))
				.setAddress(addr)
				.setPort(port)
				.getInstance();
		
		this.cycles.add(sendCycle);
		sendCycle.start();
	}

	@Override
	public void firstReceiveCallback(InetAddress receiveAddr, int port, byte[] packet)
	{
		if(!PacketProcess.CheckFullPacket(packet))
		{
			return;
		}
		
		String macAddr = PacketProcess.GetpacketMacAddr(packet);
		String name = PacketProcess.GetPacketName(packet);
		
		for(IReceiveHandler handler : this.receiveHandlers)
		{
			IPacketCycleUser user = handler.createConnection(macAddr, name);
			if(user == null)
			{
				continue;
			}
			
			AbsCycle receiveCycle = new ReceiveCycleBuilder()
					.setPacketData(packet)
					.setUserCallback(user)
					.setEndCycleCallback(this)
					.setSender(this.sendController)
					.setReceiver(this.receiveController)
					.setPacketFullUID(PacketProcess.GetPacketFULLUID(packet))
					.setAddress(receiveAddr)
					.setPort(port)
					.getInstance();
			
			this.cycles.add(receiveCycle);
			receiveCycle.start();
			break;
		}
		
	}
	
	@Override
	public void endCycleCallback(AbsCycle cycle)
	{
		this.cycles.remove(cycle);
	}
	
	public static class ConnectionManagerBuilder implements IMultiThreadTaskCallback
	{
		private final ConnectionManager instance;
		private final List<DatagramSocket> createdSocketList;
		private ReceiveControllerBuildManager rcvBuildManager;
		private IFirstReceiveObserver receiveRegister;
		
		public ConnectionManagerBuilder(ConnectionManager instance)
		{
			this.instance = instance;
			this.createdSocketList = new ArrayList<DatagramSocket>();
		}

		@Override
		public void executeTask(TaskOption option, NextTask nextTask)
		{
			if(option == TaskOption.START)
			{
				this.start(nextTask);
			}
			else if(option == TaskOption.SHUTDOWN)
			{
				this.shutdown(nextTask);
			}
		}
		
		public void setReceiveRegister(IFirstReceiveObserver receiveRegister)
		{
			this.receiveRegister = receiveRegister;
		}
		
		private void start(NextTask nextTask)
		{
			PLC_IoT_Core.CONS.push("로우 레벨 통신 관리자 로드 시작.");
			
			ReceiveController receiveController = new ReceiveController(this.receiveRegister);
			this.rcvBuildManager = new ReceiveControllerBuildManager(receiveController);
			SendController sendController = new SendController(this.createSocketList(UDP_SND_PORT_START, UDP_SND_PORT_END));
			this.rcvBuildManager.setDatagramSocket(this.createSocketList(UDP_RCV_PORT_START, UDP_RCV_PORT_END));
			
			this.instance.receiveController = receiveController;
			this.instance.sendController = sendController;
			
			nextTask.insertTask((TaskOption option, NextTask startNext)->
			{
				PLC_IoT_Core.CONS.push("로우 레벨 통신 관리자 활성화.");
				startNext.nextTask();
				
			});
			
			nextTask.insertTask(this.rcvBuildManager);
			
			nextTask.nextTask();
		}
		
		private void shutdown(NextTask nextTask)
		{
			PLC_IoT_Core.CONS.push("로우 레벨 통신 관리자 종료 시작.");
			
			nextTask.insertTask((TaskOption option, NextTask endNext)->
			{
				PLC_IoT_Core.CONS.push("로우 레벨 통신 관리자 종료 성공.");
				endNext.nextTask();
				
			});
			nextTask.insertTask(this.rcvBuildManager);
			
			for(DatagramSocket disposSocket : this.createdSocketList)
			{
				PLC_IoT_Core.CONS.push("소켓 " + disposSocket.getLocalPort() + " 번 포트 비활성화.");
				disposSocket.close();
			}
			nextTask.nextTask();
		}
		
		private List<DatagramSocket> createSocketList(int portStart, int portEnd)
		{
			List<DatagramSocket> socketList = new ArrayList<DatagramSocket>();
			for(int i = portStart; i <= portEnd; ++i)
			{
				try
				{
					DatagramSocket createSocket = new DatagramSocket(i);
					socketList.add(createSocket);
					this.createdSocketList.add(createSocket);
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
