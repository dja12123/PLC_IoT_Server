package kr.dja.plciot.WebIO.Data;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Database.IDatabaseHandler;
import kr.dja.plciot.WebConnector.IWebSocketObserver;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.AbsWebSender;
import kr.dja.plciot.WebIO.WebIOProcess;

public class DeviceInfoChange extends AbsWebSender
{
	private static final String DEVICE_INFO_CHANGE_REQ = "DeviceInfoChange";
	
	private static final String DATA_RESEND = "DeviceInfoCheck";
	private static final String INFO_CHANGE_OK = "Ok";
	private static final String INFO_CHANGE_ERROR = "Error";
	private static final String INFO_CHANGE_ERRORDB = "ErrorDB";
	
	private final IDatabaseHandler dbHandler;
	
	public DeviceInfoChange(IWebSocketReceiveObservable webSocketHandler, IDatabaseHandler dbHandler)
	{
		super(webSocketHandler);
		this.dbHandler = dbHandler;
		
		this.webSocketHandler.addObserver(DeviceInfoChange.DEVICE_INFO_CHANGE_REQ, this);
	}
	
	@Override
	public void messageReceive(Channel ch, String key, String data)
	{
		PLC_IoT_Core.CONS.push("장치 속성 변경 " + data);
		
		ch.writeAndFlush(WebIOProcess.CreateDataPacket(DATA_RESEND, INFO_CHANGE_OK));
	}

	@Override
	public void channelDisconnect(Channel ch)
	{
		// TODO Auto-generated method stub
		
	}
}
