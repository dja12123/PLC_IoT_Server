package kr.dja.plciot.DeviceConnection;

import java.util.Map;

public interface INetworkManager
{
	public void sendData(String name, Map<String, String> sendData);
	public void addReceiveObserver(String name, IDevicePacketReceiveObserver observer);
}
