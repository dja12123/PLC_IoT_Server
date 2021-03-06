package kr.dja.plciot.WebIO.Data;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.IDeviceHandler;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.WebConnector.IWebSocketObserver;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebConnector.WebServer;
import kr.dja.plciot.WebIO.AbsWebSender;

public class DevicePowerChange extends AbsWebSender
{
	private static final String DEVICE_POWER_CHANGE_REQ = "DevicePowerChange";
	
	private IDeviceHandler deviceList;
	
	public DevicePowerChange(IWebSocketReceiveObservable webSocketHandler, IDeviceHandler deviceList)
	{
		super(webSocketHandler);
		this.deviceList = deviceList;
		
		this.webSocketHandler.addObserver(DEVICE_POWER_CHANGE_REQ, this);
	}
	
	@Override
	public void websocketEvent(Channel ch, String key, String data)
	{
		if(key.equals(DEVICE_POWER_CHANGE_REQ))
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

	}

	@Override
	public void channelDisconnect(Channel ch)
	{
		
		
	}

}
