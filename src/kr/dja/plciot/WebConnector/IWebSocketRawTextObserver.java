package kr.dja.plciot.WebConnector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface IWebSocketRawTextObserver
{
	public void rawMessageReceive(Channel ch, String str);
	public void rawChannelDisconnect(Channel ch);
}
