package kr.dja.plciot.WebIO.DataFlow.DeviceRealtimePowerChange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.DeviceManager;
import kr.dja.plciot.Device.IDeviceView;
import kr.dja.plciot.WebConnector.IWebSocketObserver;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataManager;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataMember;

public class RealtimePowerChangeManager extends AbsWebFlowDataManager
{
	public static final String GRAPH_REQ = "GETGRAPH";
	private final IDeviceView deviceList;
	
	public RealtimePowerChangeManager(IDeviceView deviceList)
	{
		this.deviceList = deviceList;
	}

	@Override
	protected AbsWebFlowDataMember getMember(Channel ch, String data)
	{
		return new RealtimePowerChangeSender(ch, this.deviceList);
	}
}
