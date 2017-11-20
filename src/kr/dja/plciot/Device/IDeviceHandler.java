package kr.dja.plciot.Device;

import java.util.Iterator;

import kr.dja.plciot.Device.AbsDevice.AbsDevice;

public interface IDeviceHandler
{
	public Iterator<AbsDevice> getIterator();
	public AbsDevice getDeviceFromMac(String mac);
	
	public void addObserver(String key, IDeviceEventObserver observer);
	public void deleteObserver(IDeviceEventObserver observer);
	public void deleteObserver(IDeviceEventObserver observer, String str);
}
