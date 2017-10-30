package kr.dja.plciot.Device.TaskManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kr.dja.plciot.Device.AbsDevice.AbsDevice;

public abstract class DeviceTaskHandler
{
	protected List<AbsDevice> controlDeviceList;
	
	public DeviceTaskHandler()
	{
		this.controlDeviceList = Collections.synchronizedList(new ArrayList<AbsDevice>());
	}

	public void registerDevice(AbsDevice device)
	{
		this.controlDeviceList.add(device);
	}
	
	public void deRegisterDevice(AbsDevice device)
	{
		this.controlDeviceList.remove(device);
	}
}
