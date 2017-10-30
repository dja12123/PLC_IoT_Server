package kr.dja.plciot.Device.AbsDevice.DataFlow;

import java.net.InetAddress;
import java.util.Map;

import kr.dja.plciot.Device.AbsDevice.AbsDevice;

public abstract class AbsDataFlowDevice extends AbsDevice
{

	public AbsDataFlowDevice(String macAddr)
	{
		super(macAddr);
		
	}

	@Override
	public void packetSendCallback(boolean success, String name, String data)
	{
		
	}

	@Override
	public void packetReceiveCallback(InetAddress addr, String macAddr, String name, String data)
	{
		
	}
	
	public abstract Map<String, Integer> getDeviceValues();
	
}
