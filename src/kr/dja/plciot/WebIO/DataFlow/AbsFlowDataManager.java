package kr.dja.plciot.WebIO.DataFlow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.netty.channel.Channel;
import kr.dja.plciot.WebConnector.IWebSocketObserver;

public abstract class AbsFlowDataManager implements IWebSocketObserver
{
	private final List<AbsWebFlowDataMember> senderList;
	
	public AbsFlowDataManager()
	{
		this.senderList = Collections.synchronizedList(new ArrayList<AbsWebFlowDataMember>());
		
	}
	
	@Override
	public void messageReceive(Channel ch, String key, String data)
	{
		AbsFlowDataManager member = this.getMember();
		
	}
	
	protected abstract AbsFlowDataManager getMember();
}
