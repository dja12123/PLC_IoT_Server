package kr.dja.plciot.WebIO.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.netty.channel.Channel;
import kr.dja.plciot.Database.IDatabaseHandler;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebConnector.WebServer;
import kr.dja.plciot.WebIO.AbsWebSender;
import kr.dja.plciot.WebIO.WebIOProcess;

public class RemoteSQL extends AbsWebSender
{
	private IDatabaseHandler dbHandler;
	
	private static final String REQ_REMOTE_SQL = "RemoteSql";
	private static final String SQL_RESEND = "RemoteSqlResult";
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";
	
	public RemoteSQL(IWebSocketReceiveObservable webSocketHandler, IDatabaseHandler dbHandler)
	{
		super(webSocketHandler);
		this.dbHandler = dbHandler;
		this.webSocketHandler.addObserver(REQ_REMOTE_SQL, this);
	}

	@Override
	public void websocketEvent(Channel ch, String key, String data)
	{
		if(key.equals(REQ_REMOTE_SQL))
		{
			String[] dataSet = data.split(WebServer.VALUE_SEPARATOR);
			String dataKey = dataSet[0];
			String sqlQuery = dataSet[1];
			ResultSet rs = this.dbHandler.sqlQuery(sqlQuery);
			if(rs == null)
			{
				ch.writeAndFlush(WebIOProcess.CreateDataPacket(SQL_RESEND, ERROR));
				return;
			}
			
			JSONArray jsonArray = new JSONArray();
			String resultStr = SUCCESS + WebServer.VALUE_SEPARATOR + dataKey + WebServer.VALUE_SEPARATOR;
			
			try
			{
				while(rs.next())
				{
					int rowCount = rs.getMetaData().getColumnCount();
					
					JSONArray tempArray = new JSONArray();
					for(int i = 1; i <= rowCount; ++i)
					{
						JSONObject element = new JSONObject();
						
						element.put(rs.getMetaData().getColumnLabel(i).toLowerCase(),rs.getObject(i));
						
						tempArray.put(element);
					}
					
					jsonArray.put(tempArray);
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
			
			resultStr += jsonArray.toString();
			ch.writeAndFlush(WebIOProcess.CreateDataPacket(SQL_RESEND, resultStr));
		}
	}

	@Override
	public void channelDisconnect(Channel ch)
	{
		
	}

}
