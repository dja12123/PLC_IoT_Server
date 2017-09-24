package kr.dja.plciot.WebConnector;

public interface IWebSocketReceiveObservable
{
	public void addObserver(String key, IWebSocketObserver o);
	public void deleteObserver(String key);
}
