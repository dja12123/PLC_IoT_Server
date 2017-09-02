package kr.dja.plciot.Device.Connection;

import java.util.Map;

public interface IDevicePacketReceiveObserver
{
	public void ReceiveData(String name, Map<String, String> data, boolean success);
}
