package kr.dja.plciot.WebIO.Data;

import io.netty.channel.Channel;
import kr.dja.plciot.Database.IDatabaseHandler;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.AbsWebSender;

public class DeviceRegister extends AbsWebSender
{
	private static final String REQ_DEVICE_REGISTER = "DeviceRegister";
	
	private final IDatabaseHandler dbHandler;
	
	public DeviceRegister(IWebSocketReceiveObservable webSocketHandler, IDatabaseHandler dbHandler)
	{
		super(webSocketHandler);
		this.dbHandler = dbHandler;
	}

	@Override
	public void websocketEvent(Channel ch, String key, String data)
	{
		if(key.equals(REQ_DEVICE_REGISTER))
		{
			
		}
		
	}

	@Override
	public void channelDisconnect(Channel ch)
	{
		// TODO Auto-generated method stub
		
	}

}
