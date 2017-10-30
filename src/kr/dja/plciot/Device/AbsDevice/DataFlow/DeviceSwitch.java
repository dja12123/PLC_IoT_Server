package kr.dja.plciot.Device.AbsDevice.DataFlow;

import java.net.InetAddress;

import kr.dja.plciot.Device.AbsDevice.AbsDevice;

public class DeviceSwitch extends AbsDevice
{
	public static final String TYPE_NAME = "SWITCH";
	
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
	public void packetReceiveCallback(InetAddress addr, String macAddr, String name, String data)
	{
		
	}

}
