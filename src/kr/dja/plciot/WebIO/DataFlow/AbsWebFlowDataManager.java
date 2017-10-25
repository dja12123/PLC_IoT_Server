package kr.dja.plciot.WebIO.DataFlow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.netty.channel.Channel;
import kr.dja.plciot.WebConnector.IWebSocketObserver;

public abstract class AbsWebFlowDataManager implements IWebSocketObserver
{
	private final List<AbsWebFlowDataMember> senderList;
	
	public AbsWebFlowDataManager()
	{
		this.senderList = Collections.synchronizedList(new ArrayList<AbsWebFlowDataMember>());
		
	}
	
	@Override
	public final void messageReceive(Channel ch, String key, String data)
	{
		AbsWebFlowDataManager member = this.getMember();
		
	}
	
	protected abstract AbsWebFlowDataManager getMember();
}
