package kr.dja.plciot.WebIO.DataFlow.DeviceRealtimePowerChange;

import io.netty.channel.Channel;
import kr.dja.plciot.Device.IDeviceEventObserver;
import kr.dja.plciot.Device.IDeviceView;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.WebIO.WebIOProcess;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataMember;

public class RealtimePowerChangeSender extends AbsWebFlowDataMember implements IDeviceEventObserver
{
	private static final String SEND_KEY = "devicePowerChange";
	
	private final IDeviceView deviceView;

	public RealtimePowerChangeSender(Channel ch, IDeviceView deviceView)
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
		this.channel.writeAndFlush(WebIOProcess.CreateDataPacket(SEND_KEY, data));
	}
}