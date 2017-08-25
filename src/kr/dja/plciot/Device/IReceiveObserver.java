package kr.dja.plciot.Device;

import java.util.Map;

public interface IReceiveObserver
{
	public void ReceiveData(Device device, Map<String, String> data);
}
