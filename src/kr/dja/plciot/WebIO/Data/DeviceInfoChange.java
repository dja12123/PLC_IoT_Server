package kr.dja.plciot.WebIO.Data;

import io.netty.channel.Channel;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Database.IDatabaseHandler;
import kr.dja.plciot.WebConnector.IWebSocketObserver;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebConnector.WebServer;
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
	public void websocketEvent(Channel ch, String key, String data)
	{
		if(key.equals(DeviceInfoChange.DEVICE_INFO_CHANGE_REQ))
		{
			String callbackMessage = INFO_CHANGE_OK;
			PLC_IoT_Core.CONS.push("장치 속성 변경 " + data);
			
			try
			{
				String dataArr[] = data.split(WebServer.VALUE_SEPARATOR);
				
				if(dataArr.length != 4) throw new Exception(INFO_CHANGE_ERROR);
				
				String macAddr = dataArr[0];
				String deviceName = "null";
				String deviceGroup = "null";
				
				if(!dataArr[2].equals("")) deviceName = "'" + dataArr[2] + "'";
				if(!dataArr[3].equals("null")) deviceGroup = "'" + dataArr[3] + "'";
				
				int dbResult = this.dbHandler.sqlUpdate("update device set device_name = "
				+deviceName+", group_value = "+deviceGroup+" where mac_id = '"+macAddr+"';");
				
				if(dbResult == -1) throw new Exception(INFO_CHANGE_ERRORDB);
			}
			
			catch(Exception e)
			{
				callbackMessage = e.getMessage();
			}

			ch.writeAndFlush(WebIOProcess.CreateDataPacket(DATA_RESEND, callbackMessage));
		}
	}

	@Override
	public void channelDisconnect(Channel ch)
	{
		// TODO Auto-generated method stub
		
	}
}
