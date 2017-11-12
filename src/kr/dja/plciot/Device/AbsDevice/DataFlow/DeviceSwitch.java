package kr.dja.plciot.Device.AbsDevice.DataFlow;

import java.net.InetAddress;
import kr.dja.plciot.Database.IDatabaseHandler;
import kr.dja.plciot.Device.TaskManager.RealTimeDataHandler;
import kr.dja.plciot.LowLevelConnection.ISendCycleStarter;

public class DeviceSwitch extends AbsDataFlowDevice
{
	public static final String TYPE_NAME = "SWITCH";
	
	public DeviceSwitch(String macAddr, ISendCycleStarter sendManager, RealTimeDataHandler realTimeDataHandler, IDatabaseHandler dbhandler)
	{
		super(macAddr,sendManager, realTimeDataHandler, dbhandler);
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
	public int getDeviceValue(String key)
	{
		return -1;
	}

	@Override
	protected void storeValue(String data)
	{
		
	}
}
