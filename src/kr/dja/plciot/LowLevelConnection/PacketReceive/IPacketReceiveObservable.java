package kr.dja.plciot.LowLevelConnection.PacketReceive;

public interface IPacketReceiveObservable
{
	public void addObserver(String uuid, IPacketReceiveObserver o);
	public void deleteObserver(String uuid);
}
