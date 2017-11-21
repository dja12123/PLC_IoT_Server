package kr.dja.plciot.WebIO.DataFlow.DeviceRealtimePowerChange;

import io.netty.channel.Channel;
import kr.dja.plciot.Device.IDeviceEventObserver;
import kr.dja.plciot.Device.IDeviceHandler;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.WebConnector.WebServer;
import kr.dja.plciot.WebIO.WebIOProcess;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataMember;

public class RealtimePowerChangeSender extends AbsWebFlowDataMember implements IDeviceEventObserver
{	
	private final IDeviceHandler deviceView;

	public RealtimePowerChangeSender(Channel ch, IDeviceHandler deviceView)
	{
		super(ch);
		
		this.deviceView = deviceView;
		this.deviceView.addObserver(AbsDevice.DEVICE_POWER_CHANGE, this);
	}
	
	@Override
	public void endTask()
	{
		this.deviceView.deleteObserver(this);
	}

	@Override
	public void deviceEvent(AbsDevice device, String key, String data)
	{
		if(key == AbsDevice.DEVICE_POWER_CHANGE)
		{
			String sendData = device.macAddr + WebServer.VALUE_SEPARATOR + data;
			this.channel.writeAndFlush(WebIOProcess.CreateDataPacket(AbsDevice.DEVICE_POWER_CHANGE, sendData));
		}
	}
}