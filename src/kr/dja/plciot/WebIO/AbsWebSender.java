package kr.dja.plciot.WebIO;

import kr.dja.plciot.WebConnector.IWebSocketObserver;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;

public abstract class AbsWebSender implements IWebSocketObserver
{
	protected final IWebSocketReceiveObservable webSocketHandler;
	
	public AbsWebSender(IWebSocketReceiveObservable webSocketHandler)
	{
		this.webSocketHandler = webSocketHandler;
	}

	public void shutdown()
	{
		this.webSocketHandler.deleteObserver(this);
	}
}
