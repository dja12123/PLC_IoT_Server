package kr.dja.plciot.Device.Connection;

import java.util.Map;

import kr.dja.plciot.Device.Device;

public interface IDeviceCommunicationCallback
{
	void connectSuccess(Device connection, String name, Map<String, String> sendData, Map<String, String> returnData);
	void connectFail(Device connection, String name, Map<String, String> sendData);
}
