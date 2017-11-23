package kr.dja.plciot.WebIO.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.netty.channel.Channel;
import kr.dja.plciot.Database.IDatabaseHandler;
import kr.dja.plciot.Device.IDeviceHandler;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.AbsWebSender;

public class DeviceRegister extends AbsWebSender
{
	private static final String REQ_DEVICE_REGISTER = "DeviceRegister";
	
	private final IDatabaseHandler dbHandler;
	private final IDeviceHandler deviceHandler;
	
	public DeviceRegister(IWebSocketReceiveObservable webSocketHandler, IDatabaseHandler dbHandler, IDeviceHandler deviceHandler)
	{
		super(webSocketHandler);
		this.dbHandler = dbHandler;
		this.deviceHandler = deviceHandler;
		
		this.webSocketHandler.addObserver(REQ_DEVICE_REGISTER, this);
	}

	@Override
	public void websocketEvent(Channel ch, String key, String data)
	{
		if(key.equals(REQ_DEVICE_REGISTER))
		{
			ResultSet rs = dbHandler.sqlQuery("select device_type from waiting_device where macAddr = '"+data+"';");
			String deviceType = null;
			try
			{
				rs.next();
				deviceType = rs.getString(1);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			
			this.dbHandler.sqlUpdate("delete from waiting_device where macAddr = '"+data+"';");
			this.dbHandler.sqlUpdate("insert into device values(null, '"+data+"', '"+deviceType+"', null, 0);");
		}
		
	}

	@Override
	public void channelDisconnect(Channel ch)
	{
		// TODO Auto-generated method stub
		
	}

}
