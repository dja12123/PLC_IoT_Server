package kr.dja.plciot.Device.AbsDevice.DataFlow;

import kr.dja.plciot.Device.AbsDevice.AbsDevice;

public class DeviceConsent extends AbsDevice
{
	public static final String TYPE_NAME = "Consent";
	
	public DeviceConsent(String macAddr)
	{
		super(macAddr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void packetSendCallback(boolean success, String name, String data)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void packetReceiveCallback(String name, String data)
	{
		// TODO Auto-generated method stub
		
	}

}
