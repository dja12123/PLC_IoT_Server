package kr.dja.plciot.WebConnector;

import io.netty.channel.Channel;

public interface IWebSocketObserver
{
	public void messageReceive(Channel ch,String key, String data);
}
