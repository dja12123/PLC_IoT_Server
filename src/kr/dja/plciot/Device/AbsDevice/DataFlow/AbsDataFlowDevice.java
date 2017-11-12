package kr.dja.plciot.Device.AbsDevice.DataFlow;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Database.IDatabaseHandler;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.TaskManager.RealTimeDataHandler;
import kr.dja.plciot.LowLevelConnection.ISendCycleStarter;

public abstract class AbsDataFlowDevice extends AbsDevice
{
	private final RealTimeDataHandler realTimeDataHandler;
	protected final IDatabaseHandler databaseHandler;
	
	public AbsDataFlowDevice(String macAddr, ISendCycleStarter sendManager, RealTimeDataHandler realTimeDataHandler, IDatabaseHandler dbhandler)
	{
		super(macAddr, sendManager);
		this.realTimeDataHandler = realTimeDataHandler;
		this.databaseHandler = dbhandler;
	}

	@Override
	public void packetSendCallback(boolean success, String name, String data)
	{
		
	}

	@Override
	public void packetReceiveCallback(InetAddress addr, String macAddr, String name, String data)
	{
		super.packetReceiveCallback(addr, macAddr, name, data);
		switch(name)
		{
		case "sensorData":
			this.storeValue(data);
			break;
		}
	}
	
	protected abstract void storeValue(String data);
	
	public abstract int getDeviceValue(String key);
	
}
