package kr.dja.plciot.WebIO;

import io.netty.channel.Channel;
import kr.dja.plciot.WebConnector.IWebSocketObserver;

public class DevicePowerChange implements IWebSocketObserver
{
	
	public static final String DATA_REQ = "DevicePowerChange";
	
	@Override
	public void messageReceive(Channel ch, String key, String data)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void channelDisconnect(Channel ch)
	{
		// TODO Auto-generated method stub
		
	}

}
