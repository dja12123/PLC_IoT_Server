package kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph;

import java.util.Iterator;
import io.netty.channel.Channel;
import kr.dja.plciot.Device.IDeviceList;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.AbsDevice.DataFlow.AbsDataFlowDevice;
import kr.dja.plciot.WebConnector.WebServer;
import kr.dja.plciot.WebIO.WebIOProcess;

public class RealTimeGraphSender extends Thread
{
	private static final int SEND_DATA_INTERVAL = 100;
	private static final String SEND_KEY = "SERVER_REALTIME_DATA";
	
	private final IDeviceList deviceList;
	
	private final Channel ch;
	private final String dataKey;
	
	private boolean runFlag;
	
	public RealTimeGraphSender(Channel ch, String dataKey, IDeviceList deviceList)
	{
		this.deviceList = deviceList;
		
		this.ch = ch;
		this.dataKey = dataKey;
		
		this.runFlag = true;
		this.start();
	}
	
	@Override
	public void run()
	{
		while(this.runFlag && this.ch.isActive())
		{
			Iterator<AbsDevice> itr = this.deviceList.getIterator();
			int count = 0;
			int sum = 0;
			while(itr.hasNext())
			{
				AbsDevice absDevice = itr.next();
				if(!(absDevice instanceof AbsDataFlowDevice)) continue;
				AbsDataFlowDevice dataFlowDevice = (AbsDataFlowDevice)absDevice;
				
				int deviceValue = dataFlowDevice.getDeviceValue(this.dataKey);
				if(deviceValue == -1) continue;
				
				++count;
				sum += deviceValue;
			}
			String sendData = count + WebServer.KEY_SEPARATOR + sum;
			
			this.ch.writeAndFlush(WebIOProcess.CreateDataPacket(SEND_KEY, sendData));
			
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