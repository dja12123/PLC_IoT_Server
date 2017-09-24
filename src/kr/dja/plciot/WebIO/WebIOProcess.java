package kr.dja.plciot.WebIO;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import kr.dja.plciot.WebConnector.WebServer;

public class WebIOProcess
{
	
	public static Object CreateDataPacket(String key, Object data)
	{
		return new TextWebSocketFrame(key + WebServer.SEPARATOR + data.toString());
	}
	
	public static Object CreateDataPacket(String key)
	{
		return new TextWebSocketFrame(key + WebServer.SEPARATOR);
	}
}
