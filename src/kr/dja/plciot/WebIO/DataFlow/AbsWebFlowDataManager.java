package kr.dja.plciot.WebIO.DataFlow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.WebConnector.IWebSocketObserver;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.AbsWebSender;
import kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph.RealTimeGraphSender;

public abstract class AbsWebFlowDataManager extends AbsWebSender
{
	private final Map<Channel, AbsWebFlowDataMember> senderMap;
	
	public AbsWebFlowDataManager(IWebSocketReceiveObservable webSocketHandler)
	{
		super(webSocketHandler);
		this.senderMap = Collections.synchronizedMap(new HashMap<Channel, AbsWebFlowDataMember>());
	}
	
	@Override
	public void messageReceive(Channel ch, String key, String data)
	{
		PLC_IoT_Core.CONS.push("실시간 데이터 전송 시작. " + key + " " + ch);
		AbsWebFlowDataMember sender = this.getMember(ch, data);
		this.senderMap.put(ch, sender);
	}
	
	@Override
	public void channelDisconnect(Channel ch)
	{
		PLC_IoT_Core.CONS.push("실시간 데이터 전송 종료. " + ch);
		AbsWebFlowDataMember sender = this.senderMap.get(ch);
		sender.endTask();
	}
	
	public void shutdown()
	{
		super.shutdown();
		for(Channel key : this.senderMap.keySet())
		{
			AbsWebFlowDataMember sender = this.senderMap.get(key);
			sender.endTask();
		}
		PLC_IoT_Core.CONS.push("실시간 전송 관리자 종료.");
	}
	
	protected abstract AbsWebFlowDataMember getMember(Channel ch, String data);
}
