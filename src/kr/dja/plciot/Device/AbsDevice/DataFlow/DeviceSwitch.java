package kr.dja.plciot.Device.AbsDevice.DataFlow;

import kr.dja.plciot.Device.AbsDevice.AbsDevice;

public class DeviceSwitch extends AbsDevice
{

	public DeviceSwitch(String macAddr)
	{
		super(macAddr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void packetSendCallback(boolean success, String name, String data)
	{
		
		
	}

	@Override
	public void packetReceiveCallback(String name, String data)
	{
		
		
	}

}
