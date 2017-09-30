package kr.dja.plciot.WebIO.RealTimeGraph;

import java.util.List;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.WebIO.WebIOProcess;

public class RealTimeGraphSender extends Thread
{
	private static final int SEND_DATA_INTERVAL = 100;
	private static final String SEND_KEY = "SERVER_REALTIME_DATA";
	
	private final List<RealTimeGraphSender> senderList;
	private final Channel ch;
	private final String data;
	
	private boolean runFlag;
	
	int i = 0;
	
	public RealTimeGraphSender(List<RealTimeGraphSender> senderList, Channel ch, String data)
	{
		this.senderList = senderList;
		this.ch = ch;
		this.data = data;
		
		this.senderList.add(this);
		this.runFlag = true;
		this.start();
	}
	
	@Override
	public void run()
	{
		PLC_IoT_Core.CONS.push("실시간 그래프 전송시작.("+this.senderList.size()+")");
		while(this.runFlag && this.ch.isActive())
		{
			++i;
			if(i > 10)
			{
				i = 0;
			}
			this.ch.writeAndFlush(WebIOProcess.CreateDataPacket(SEND_KEY, i));
			try
			{
				Thread.sleep(SEND_DATA_INTERVAL);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}
		this.ch.close();
		this.senderList.remove(this);
		PLC_IoT_Core.CONS.push("실시간 그래프 전송종료.("+this.senderList.size()+")");
	}
	
	public void endTask()
	{
		this.runFlag = false;
	}
}
