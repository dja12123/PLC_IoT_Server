package kr.dja.plciot.WebConnector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface IWebSocketRawTextObserver
{
	public void messageReceive(Channel ch, String str);
}
