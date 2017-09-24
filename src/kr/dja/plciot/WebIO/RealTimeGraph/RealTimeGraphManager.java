package kr.dja.plciot.WebIO.RealTimeGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.netty.channel.Channel;
import kr.dja.plciot.WebConnector.IWebSocketObserver;

public class RealTimeGraphManager implements IWebSocketObserver
{
	private final List<RealTimeGraphSender> senderList;
	
	public RealTimeGraphManager()
	{
		this.senderList = Collections.synchronizedList(new ArrayList<RealTimeGraphSender>());
	}

	@Override
	public void messageReceive(Channel ch, String key, String data)
	{
		new RealTimeGraphSender(senderList, ch, data);
		
	}
	
	public void shutdown()
	{
		while(!senderList.isEmpty())
		{
			RealTimeGraphSender sender = senderList.get(0);
			sender.endTask();
			try
			{
				sender.join();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
