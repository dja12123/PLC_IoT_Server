package kr.dja.plciot.Device;

import kr.dja.plciot.Device.AbsDevice.AbsDevice;

public interface IDeviceEventObserver
{
	public void deviceEvent(AbsDevice device, String key, String data);
}
