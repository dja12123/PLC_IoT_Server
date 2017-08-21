package kr.dja.plciot.Device.Connection;

import java.util.Map;

public interface IDeviceConnectSuccessCallback
{
	void connectSuccess(DeviceConnect connection, String name, Map<String, String> sendData, Map<String, String> returnData);
}
