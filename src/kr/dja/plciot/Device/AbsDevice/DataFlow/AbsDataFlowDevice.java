package kr.dja.plciot.Device.AbsDevice.DataFlow;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Database.IDatabaseHandler;
import kr.dja.plciot.Device.IDeviceEventObserver;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.LowLevelConnection.ISendCycleStarter;

public abstract class AbsDataFlowDevice extends AbsDevice
{
	public static final String SENSOR_DATA_EVENT = "sensorData";
	
	protected final IDatabaseHandler databaseHandler;
	
	public AbsDataFlowDevice(String macAddr, ISendCycleStarter sendManager, IDeviceEventObserver eventObserver,
			IDatabaseHandler dbhandler)
	{
		super(macAddr, sendManager, eventObserver);
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
		case SENSOR_DATA_EVENT:
			this.storeValue(data);
			this.eventObserver.deviceEvent(this, SENSOR_DATA_EVENT, data);
			break;
		}
	}
	
	public abstract String getDeviceType();
	
	protected abstract void storeValue(String data);
	
	public abstract int getDeviceValue(String key);
}
