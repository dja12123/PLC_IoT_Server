package kr.dja.plciot.Device;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.Connection.IReceiveRegister;
import kr.dja.plciot.Device.Connection.PacketProcess;
import kr.dja.plciot.Device.Connection.ReceiveCycle;
import kr.dja.plciot.Device.Connection.PacketReceive.IPacketReceiveObservable;
import kr.dja.plciot.Device.Connection.PacketReceive.ReceiveController;
import kr.dja.plciot.Device.Connection.PacketReceive.ReceiveController.ReceiveControllerBuildManager;
import kr.dja.plciot.Device.Connection.PacketSend.SendController;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;
import kr.dja.plciot.Device.Connection.PacketReceive.IPacketReceiveObserver;

public class DeviceManager implements IReceiveRegister, IMultiThreadTaskCallback
{
	private static final int UDP_RCV_PORT_START = 50000;
	private static final int UDP_RCV_PORT_END = 50010;
	private static final int UDP_SND_PORT_START = 50011;
	private static final int UDP_SND_PORT_END = 50020;
	
	// 디바이스 통신모듈.
	private ReceiveController receiveController;
	private SendController sendController;
	
	private final Map<String, Device> deviceList;
	
	public DeviceManager()
	{
		this.deviceList = new HashMap<String, Device>();
	}

	@Override
	public void registerReceive(IPacketReceiveObservable observable, byte[] data)
	{
		String macAddr = PacketProcess.GetpacketMacAddr(data);
		Device receiveTarget = this.deviceList.getOrDefault(macAddr, null);
		if(receiveTarget != null)
		{// 일반적인 통신 사이클을 시작합니다.
			ReceiveCycle receiveCycle = new ReceiveCycle(observable, data, receiveTarget);
		}
		else
		{// 장치 등록 사이클을 시작합니다.
			PLC_IoT_Core.CONS.push("등록되지 않은 장치 접근.");
		}
		
	}

	@Override
	public void executeTask(TaskOption option, NextTask nextTask)
	{// 디비접속해서 장치목록긁어오기
		if(option == TaskOption.START)
		{
			this.createCommunicationer(nextTask);
		}	
	}
	
	private void createCommunicationer(NextTask nextTask)
	{
		PLC_IoT_Core.CONS.push("장치 통신 관리자 로드 시작.");
		
		nextTask.insertTask((TaskOption option, NextTask startNext)->{
			this.sendController = new SendController(UDP_SND_PORT_START, UDP_SND_PORT_END);
			PLC_IoT_Core.CONS.push("장치 통신 관리자 활성화.");
			
			String testPacketUID = PacketProcess.CreateUUID("001F1F1F1FAA");
			
			this.receiveController.addObserver(testPacketUID, (byte[] packet)->
			{
				PLC_IoT_Core.CONS.push("장치 통신 테스트 완료.");
				PLC_IoT_Core.CONS.push(PacketProcess.GetPacketName(packet));
				startNext.nextTask();
			});
			
			byte[] testPacket = PacketProcess.CreateDataSet();
			PacketProcess.InputPacketHeader(testPacket, testPacketUID, PacketProcess.PHASE_CHECK);
			try
			{
				this.sendController.sendData(InetAddress.getByName("127.0.0.1"), testPacket);
			}
			catch(Exception e){};
		});
		
		ReceiveControllerBuildManager builder = new ReceiveControllerBuildManager(this);
		builder.setDatagramSocket(UDP_RCV_PORT_START, UDP_RCV_PORT_END);
		builder.createInstance(nextTask, (ReceiveController instance)->
		{
			this.receiveController = instance;
			
		});
		
	}
	
}
