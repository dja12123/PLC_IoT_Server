package kr.dja.plciot.Device;

import java.util.Map;

public interface IDeviceConnectSuccess
{
	void connectSuccess(DeviceConnection connection, String name, Map<String, String> sendData, Map<String, String> returnData);
}
