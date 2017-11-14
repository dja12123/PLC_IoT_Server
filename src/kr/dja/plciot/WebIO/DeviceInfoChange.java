package kr.dja.plciot.WebIO;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.WebConnector.IWebSocketObserver;

public class DeviceInfoChange implements IWebSocketObserver
{
	public static final String DEVICE_INFO_CHANGE_REQ = "DeviceInfoChange";
	
	public static final String DATA_RESEND = "DeviceInfoCheck";
	public static final String INFO_CHANGE_OK = "OK";
	public static final String INFO_CHANGE_ERROR = "ERROR";
	public static final String INFO_CHANGE_ERRORDB = "ERRORDB";
	
	@Override
	public void messageReceive(Channel ch, String key, String data)
	{
		PLC_IoT_Core.CONS.push("장치 속성 변경 " + data);
		
		ch.writeAndFlush(WebIOProcess.CreateDataPacket(DATA_RESEND, INFO_CHANGE_OK));
	}

	@Override
	public void channelDisconnect(Channel ch)
	{
		// TODO Auto-generated method stub
		
	}

}
