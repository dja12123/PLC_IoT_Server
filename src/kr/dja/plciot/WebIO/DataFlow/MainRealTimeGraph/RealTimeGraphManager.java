package kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.DeviceManager;
import kr.dja.plciot.Device.IDeviceHandler;
import kr.dja.plciot.Device.AbsDevice.DataFlow.AbsDataFlowDevice;
import kr.dja.plciot.WebConnector.IWebSocketObserver;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataManager;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataMember;

public class RealTimeGraphManager extends AbsWebFlowDataManager implements IWebSocketObserver
{
	public static final String GRAPH_REQ = "getGraph";
	private final IDeviceHandler deviceView;
	
	public RealTimeGraphManager(IDeviceHandler deviceList)
	{
		this.deviceView = deviceList;
	}

	@Override
	protected AbsWebFlowDataMember getMember(Channel ch, String data)
	{
		return new RealTimeGraphSender(ch, data, this.deviceView);
	}
}
