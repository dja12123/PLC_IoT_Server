package kr.dja.plciot.WebIO;

import io.netty.channel.Channel;
import kr.dja.plciot.Device.IDeviceHandler;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.WebConnector.IWebSocketObserver;
import kr.dja.plciot.WebConnector.WebServer;

public class DevicePowerChange implements IWebSocketObserver
{
	public static final String DEVICE_POWER_CHANGE_REQ = "DevicePowerChange";
	
	private IDeviceHandler deviceList;
	
	public DevicePowerChange(IDeviceHandler deviceList)
	{
		this.deviceList = deviceList;
	}
	
	@Override
	public void messageReceive(Channel ch, String key, String data)
	{
		String dataSplit[] = data.split(WebServer.VALUE_SEPARATOR);
		AbsDevice device = deviceList.getDeviceFromMac(dataSplit[0]);
		if(device == null) return;
		
		switch(dataSplit[1])
		{
		case "on": device.setPower(true); break;
		case "off": device.setPower(false); break;
		}
	}

	@Override
	public void channelDisconnect(Channel ch)
	{
		// TODO Auto-generated method stub
		
	}

}
