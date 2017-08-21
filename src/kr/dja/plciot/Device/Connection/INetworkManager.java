package kr.dja.plciot.Device.Connection;

import java.util.Map;

public interface INetworkManager
{
	public void sendData(String name, Map<String, String> sendData, IDeviceConnectSuccessCallback successCallback, IDeviceConnectFailCallback failCallback);
	public void receiveData(String name, Map<String, String> sendData);
}
