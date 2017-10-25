package kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.WebConnector.IWebSocketObserver;

public class RealTimeGraphManager implements IWebSocketObserver
{
	private final Map<Channel, RealTimeGraphSender> senderMap;
	
	public RealTimeGraphManager()
	{
		this.senderMap = Collections.synchronizedMap(new HashMap<Channel, RealTimeGraphSender>());
	}

	@Override
	public void messageReceive(Channel ch, String key, String data)
	{
		PLC_IoT_Core.CONS.push("실시간 그래프 전송 시작. " + ch);
		RealTimeGraphSender sender = new RealTimeGraphSender(ch, data);
		this.senderMap.put(ch, sender);
	}
	
	@Override
	public void channelDisconnect(Channel ch)
	{
		PLC_IoT_Core.CONS.push("실시간 그래프 전송 종료. " + ch);
		RealTimeGraphSender sender = this.senderMap.get(ch);
		sender.endTask();
	}
	
	public void shutdown()
	{
		for(Channel key : this.senderMap.keySet())
		{
			RealTimeGraphSender sender = this.senderMap.get(key);
			sender.endTask();
			try
			{
				sender.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		PLC_IoT_Core.CONS.push("실시간 그래프 전송 관리자 종료.");
	}


}
