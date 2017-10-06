package kr.dja.plciot.WebIO.DataFlow;

import java.util.List;

import io.netty.channel.Channel;
import kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph.RealTimeGraphSender;

public abstract class AbsWebFlowDataMember
{
	List<RealTimeGraphSender> senderList;
	public AbsWebFlowDataMember(Channel ch, String data)
	{
		
	}
}
