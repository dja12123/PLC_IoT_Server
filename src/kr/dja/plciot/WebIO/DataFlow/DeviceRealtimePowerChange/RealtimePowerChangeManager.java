package kr.dja.plciot.WebIO.DataFlow.DeviceRealtimePowerChange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.DeviceManager;
import kr.dja.plciot.Device.IDeviceHandler;
import kr.dja.plciot.WebConnector.IWebSocketObserver;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataManager;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataMember;

public class RealtimePowerChangeManager extends AbsWebFlowDataManager
{
	private static final String POWER_REQ = "GetPowerChange";
	private final IDeviceHandler deviceList;
	
	public RealtimePowerChangeManager(IWebSocketReceiveObservable webSocketHandler, IDeviceHandler deviceList)
	{
		super(webSocketHandler);
		this.deviceList = deviceList;
		
		this.webSocketHandler.addObserver(RealtimePowerChangeManager.POWER_REQ, this);
	}

	@Override
	protected AbsWebFlowDataMember getMember(Channel ch, String data)
	{
		return new RealtimePowerChangeSender(ch, this.deviceList);
	}
}
