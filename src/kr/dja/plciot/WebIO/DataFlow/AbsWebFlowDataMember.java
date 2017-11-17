package kr.dja.plciot.WebIO.DataFlow;

import java.util.List;

import io.netty.channel.Channel;
import kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph.RealTimeGraphSender;

public abstract class AbsWebFlowDataMember
{
	protected final Channel channel;
	
	public AbsWebFlowDataMember(Channel ch)
	{
		this.channel = ch;
	}
	public abstract void endTask();
}
