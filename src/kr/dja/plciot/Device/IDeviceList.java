package kr.dja.plciot.Device;

import java.util.Iterator;

import kr.dja.plciot.Device.AbsDevice.AbsDevice;

public interface IDeviceList
{
	public Iterator<AbsDevice> getIterator();
	public AbsDevice getDeviceFromMac(String mac);
}
