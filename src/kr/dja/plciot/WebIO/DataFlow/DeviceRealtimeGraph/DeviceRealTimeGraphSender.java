package kr.dja.plciot.WebIO.DataFlow.DeviceRealtimeGraph;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.IDeviceEventObserver;
import kr.dja.plciot.Device.IDeviceHandler;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.AbsDevice.DataFlow.AbsDataFlowDevice;
import kr.dja.plciot.WebConnector.IWebSocketObserver;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.WebIOProcess;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataMember;

public class DeviceRealTimeGraphSender extends AbsWebFlowDataMember implements IDeviceEventObserver, Runnable, IWebSocketObserver
{
	private static final int SEND_DATA_INTERVAL = 200;
	private static final String SEND_KEY = "serverRealtimeData";
	private static final String REQ_TYPE_CHANGE = "DeviceGraphTypeChange";
	private static final String REQ_DEVICE_CHANGE = "DeviceGraphDeviceChange";
	
	private final IWebSocketReceiveObservable webSocketHandler;
	private final IDeviceHandler deviceView;
	
	private String dataKey;
	
	private boolean runFlag;
	
	private final Thread thread;
	
	private int sum;
	private int dataCount;
	
	private String sendData;
	
	public DeviceRealTimeGraphSender(Channel ch, String dataKey
			, IWebSocketReceiveObservable webSocketHandler, IDeviceHandler deviceView)
	{
		super(ch);
		this.webSocketHandler = webSocketHandler;
		this.deviceView = deviceView;
		
		this.webSocketHandler.addObserver(REQ_TYPE_CHANGE, this);
		this.deviceView.addObserver(AbsDataFlowDevice.SENSOR_DATA_EVENT, this);
		
		this.dataKey = dataKey;
		
		this.runFlag = true;
		
		this.thread = new Thread(this);
		
		this.sum = 0;
		this.dataCount = 0;
		
		this.sendData = "0";
		
		this.thread.start();
	}
	
	@Override
	public void run()
	{
		while(this.runFlag && this.channel.isActive())
		{
			if(this.dataCount > 0)
			{
				this.sendData = Integer.toString(this.sum/this.dataCount);
				this.sum = 0;
				this.dataCount = 0;
			}
			
			this.channel.writeAndFlush(WebIOProcess.CreateDataPacket(SEND_KEY, this.sendData));
			try
			{
				Thread.sleep(SEND_DATA_INTERVAL);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}
		this.channel.close();
	}
	
	public void endTask()
	{
		this.thread.interrupt();
		try
		{
			this.thread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		this.webSocketHandler.deleteObserver(this);
		this.deviceView.deleteObserver(this, AbsDataFlowDevice.SENSOR_DATA_EVENT);
		this.runFlag = false;
	}

	@Override
	public void deviceEvent(AbsDevice device, String key, String data)
	{
		if(!(device instanceof AbsDataFlowDevice)) return;
		AbsDataFlowDevice dataflowDevice = (AbsDataFlowDevice)device;
		int deviceData = dataflowDevice.getDeviceValue(this.dataKey);
		if(deviceData == -1) return;
		this.sum += deviceData;
		++this.dataCount;
	}

	@Override
	public void websocketEvent(Channel ch, String key, String data)
	{
		if(key.equals(REQ_TYPE_CHANGE))
		{
			this.dataKey = data;
		}
	}

	@Override
	public void channelDisconnect(Channel ch){}
}