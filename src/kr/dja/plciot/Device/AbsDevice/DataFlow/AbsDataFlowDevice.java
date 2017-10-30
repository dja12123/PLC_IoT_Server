package kr.dja.plciot.Device.AbsDevice.DataFlow;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.TaskManager.RealTimeDataHandler;

public abstract class AbsDataFlowDevice extends AbsDevice
{
	private final RealTimeDataHandler realTimeDataHandler;
	
	public AbsDataFlowDevice(String macAddr, RealTimeDataHandler realTimeDataHandler)
	{
		super(macAddr);
		this.realTimeDataHandler = realTimeDataHandler;
	}

	@Override
	public void packetSendCallback(boolean success, String name, String data)
	{
		
	}

	@Override
	public void packetReceiveCallback(InetAddress addr, String macAddr, String name, String data)
	{
		switch(name)
		{
		case "sensorData":
			this.storeValue(data);
			break;
		}
	}
	
	protected abstract void storeValue(String data);
	
	protected abstract void getDeviceValues(Map<String, Integer> map);
	
}
