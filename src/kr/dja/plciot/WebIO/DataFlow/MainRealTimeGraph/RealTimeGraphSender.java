package kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph;

import java.util.HashMap;
import java.util.List;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.DeviceManager;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.AbsDevice.DataFlow.AbsDataFlowDevice;
import kr.dja.plciot.WebIO.WebIOProcess;

public class RealTimeGraphSender extends Thread
{
	private static final int SEND_DATA_INTERVAL = 100;
	private static final String SEND_KEY = "SERVER_REALTIME_DATA";
	
	private final DeviceManager deviceManager;
	
	private final Channel ch;
	private final String data;
	
	private boolean runFlag;
	
	public RealTimeGraphSender(Channel ch, String data, DeviceManager deviceManager)
	{
		this.deviceManager = deviceManager;
		
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
			AbsDevice device = deviceManager.getDeviceMap().getOrDefault("1A2B3C4D5E6E", null);
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			if(device != null)
			{
				((AbsDataFlowDevice)device).getDeviceValues(map);
				int value = map.getOrDefault("Power", 0);
				System.out.println("°ª:" + value);
				this.ch.writeAndFlush(WebIOProcess.CreateDataPacket(SEND_KEY, value));
			}
			
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
