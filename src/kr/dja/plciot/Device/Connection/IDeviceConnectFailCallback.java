package kr.dja.plciot.Device.Connection;

import java.util.Map;

public interface IDeviceConnectFailCallback
{
	void connectFail(DeviceConnect connection, String name, Map<String, String> sendData);
}
