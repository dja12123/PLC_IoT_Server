package kr.dja.plciot.WebConnector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketHandler extends ChannelInboundHandlerAdapter
{
	private final IWebSocketRawTextObserver observer;
	
	public WebSocketHandler(IWebSocketRawTextObserver observer)
	{
		this.observer = observer;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{

		if (msg instanceof WebSocketFrame)
		{
			System.out.println("This is a WebSocket frame");
			System.out.println("Client Channel : " + ctx.channel());
			if (msg instanceof BinaryWebSocketFrame)
			{
				System.out.println("BinaryWebSocketFrame Received : ");
				System.out.println(((BinaryWebSocketFrame) msg).content());
			}
			else if (msg instanceof TextWebSocketFrame)
			{
				System.out.println("TextWebSocketFrame Received : ");
				this.observer.messageReceive(ctx.channel(), ((TextWebSocketFrame) msg).text());
			}
			else if (msg instanceof PingWebSocketFrame)
			{
				System.out.println("PingWebSocketFrame Received : ");
				System.out.println(((PingWebSocketFrame) msg).content());
			}
			else if (msg instanceof PongWebSocketFrame)
			{
				System.out.println("PongWebSocketFrame Received : ");
				System.out.println(((PongWebSocketFrame) msg).content());
			}
			else if (msg instanceof CloseWebSocketFrame)
			{
				System.out.println("CloseWebSocketFrame Received : ");
				System.out.println("ReasonText :" + ((CloseWebSocketFrame) msg).reasonText());
				System.out.println("StatusCode : " + ((CloseWebSocketFrame) msg).statusCode());
			}
			else
			{
				System.out.println("Unsupported WebSocketFrame");
			}
		}
	}
	

}