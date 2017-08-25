package kr.dja.plciot.Device;

import java.util.Map;

import kr.dja.plciot.Device.Connection.IDeviceCommunicationCallback;

public interface INetworkManager
{
	public void sendData(String name, Map<String, String> sendData, IDeviceCommunicationCallback callback);
	public void addReceiveObserver(String name, IReceiveObserver observer);
}
