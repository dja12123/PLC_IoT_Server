package kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph;

import java.util.List;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.WebIO.WebIOProcess;

public class RealTimeGraphSender extends Thread
{
	private static final int SEND_DATA_INTERVAL = 100;
	private static final String SEND_KEY = "SERVER_REALTIME_DATA";
	
	private final Channel ch;
	private final String data;
	
	private boolean runFlag;
	
	int i = 0;
	
	public RealTimeGraphSender(Channel ch, String data)
	{
		this.ch = ch;
		this.data = data;
		
		this.runFlag = true;
		this.start();
	}
	
	@Override
	public void run()
	{
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
	}
	
	public void endTask()
	{
		this.interrupt();
		this.runFlag = false;
	}
}
