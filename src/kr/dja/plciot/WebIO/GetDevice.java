package kr.dja.plciot.WebIO;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.WebConnector.IWebSocketObserver;

public class GetDevice implements IWebSocketObserver
{
	public static final String DATA_REQ = "getDeviceData";
	
	@Override
	public void messageReceive(Channel ch, String key, String data)
	{
		PLC_IoT_Core.CONS.push("장치 데이터 요청 " + data);
	}

	@Override
	public void channelDisconnect(Channel ch)
	{
		// TODO Auto-generated method stub
		
	}

}
