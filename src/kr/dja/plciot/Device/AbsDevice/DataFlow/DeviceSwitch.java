package kr.dja.plciot.Device.AbsDevice.DataFlow;

import java.net.InetAddress;
import java.util.Map;

import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.TaskManager.RealTimeDataHandler;

public class DeviceSwitch extends AbsDataFlowDevice
{
	public static final String TYPE_NAME = "SWITCH";
	
	public DeviceSwitch(String macAddr, RealTimeDataHandler realTimeDataHandler)
	{
		super(macAddr, realTimeDataHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void packetSendCallback(boolean success, String name, String data)
	{
		super.packetSendCallback(success, name, data);
		
	}

	@Override
	public void packetReceiveCallback(InetAddress addr, String macAddr, String name, String data)
	{
		super.packetReceiveCallback(addr, macAddr, name, data);
		
	}

	@Override
	public void getDeviceValues(Map<String, Integer> map)
	{
		
	}

	@Override
	protected void storeValue(String data)
	{
		// TODO Auto-generated method stub
		
	}

}
