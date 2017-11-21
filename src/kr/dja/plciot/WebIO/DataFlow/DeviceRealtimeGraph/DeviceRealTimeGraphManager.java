package kr.dja.plciot.WebIO.DataFlow.DeviceRealtimeGraph;

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
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataManager;
import kr.dja.plciot.WebIO.DataFlow.AbsWebFlowDataMember;
import kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph.RealTimeGraphManager;

public class DeviceRealTimeGraphManager extends AbsWebFlowDataManager
{
	private static final String DEVICE_GRAPH_REQ = "GetDeviceGraph";
	private final IDeviceHandler deviceView;
	
	public DeviceRealTimeGraphManager(IWebSocketReceiveObservable webSocketHandler, IDeviceHandler deviceList)
	{
		super(webSocketHandler);
		this.deviceView = deviceList;
		
		this.webSocketHandler.addObserver(RealTimeGraphManager.GRAPH_REQ, this);
	}

	@Override
	protected AbsWebFlowDataMember getMember(Channel ch, String data)
	{
		return new DeviceRealTimeGraphSender(ch, data, this.deviceView);
	}
}
