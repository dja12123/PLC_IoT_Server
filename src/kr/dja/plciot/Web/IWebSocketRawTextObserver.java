package kr.dja.plciot.Web;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface IWebSocketRawTextObserver
{
	public void messageReceive(Channel ch, String str);
}
