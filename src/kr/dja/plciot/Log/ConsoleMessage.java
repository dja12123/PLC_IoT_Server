package kr.dja.plciot.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleMessage
{// 콘솔 메세지 클래스
	
	private final Object sender;
	private final String message;
	private final Date createdTime;
	private final int index;
	
	ConsoleMessage(Object sender, String message, int index)
	{
		this.sender = sender;
		this.message = message;
		this.createdTime = new Date(System.currentTimeMillis());
		this.index = index;
	}
	
	public Object getSender()
	{
		return this.sender;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public Date getCreatedTime()
	{
		return this.createdTime;
	}
	
	public int getIndex()
	{
		return this.index;
	}
	
}
