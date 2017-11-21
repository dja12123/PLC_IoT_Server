package kr.dja.plciot.WebConnector;

public interface IWebSocketReceiveObservable
{
	public void addObserver(String key, IWebSocketObserver observer);
	public void deleteObserver(String key, IWebSocketObserver observer);
	public void deleteObserver(IWebSocketObserver observer);
}
