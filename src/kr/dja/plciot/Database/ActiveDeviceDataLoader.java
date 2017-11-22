package kr.dja.plciot.Database;

import kr.dja.plciot.Device.IDeviceEventObserver;
import kr.dja.plciot.Device.IDeviceHandler;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;

public class ActiveDeviceDataLoader implements IDeviceEventObserver
{
	private IDatabaseHandler dbHandler;
	private IDeviceHandler deviceHandler;

	public ActiveDeviceDataLoader(IDatabaseHandler dbHandler, IDeviceHandler deviceHandler)
	{
		this.dbHandler = dbHandler;
		this.deviceHandler = deviceHandler;
		//deviceHandler.addObserver(key, observer);
	}

	@Override
	public void deviceEvent(AbsDevice device, String key, String data)
	{
		
	}
}
