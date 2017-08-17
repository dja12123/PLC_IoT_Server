package kr.dja.plciot.Device;

import java.util.Map;

public interface IDeviceConnectFail
{
	void connectFail(DeviceConnection connection, String name, Map<String, String> sendData);
}
