package kr.dja.plciot.Device;

import java.util.ArrayList;
import java.util.List;

import kr.dja.plciot.Device.Connection.IReceiveRegister;
import kr.dja.plciot.Device.Connection.ReceiveCycle;
import kr.dja.plciot.Device.Connection.PacketReceive.IPacketReceiveObservable;
import kr.dja.plciot.Device.Connection.PacketReceive.ReceiveController;
import kr.dja.plciot.Device.Connection.PacketSend.SendController;

public class DeviceManager implements IReceiveRegister
{
	private final ReceiveController receiveController;
	private final SendController sendController;
	
	private final List<Device> deviceList;
	
	public DeviceManager(ReceiveController receiveController, SendController sendController)
	{
		this.receiveController = receiveController;
		this.sendController = sendController;
		this.deviceList = new ArrayList<Device>();
	}
	
	@Override
	public void registerReceive(IPacketReceiveObservable observable, byte[] data)
	{
		ReceiveCycle receiveCycle = new ReceiveCycle(observable, data);
		
		
	}
	
	
}
