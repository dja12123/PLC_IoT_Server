package kr.dja.plciot.Device;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.DeviceConnection.ConnectionManager;
import kr.dja.plciot.DeviceConnection.IReceiveRegister;
import kr.dja.plciot.DeviceConnection.PacketProcess;
import kr.dja.plciot.DeviceConnection.ReceiveCycle;
import kr.dja.plciot.DeviceConnection.PacketReceive.IPacketReceiveObservable;
import kr.dja.plciot.DeviceConnection.PacketReceive.ReceiveController;
import kr.dja.plciot.DeviceConnection.PacketReceive.ReceiveController.ReceiveControllerBuildManager;
import kr.dja.plciot.DeviceConnection.PacketSend.SendController;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class DeviceManager implements IReceiveRegister
{
	private final ConnectionManager connectionManager;
	private final Map<String, Device> deviceList;
	
	public DeviceManager(ConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
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
	
}
