package kr.dja.plciot.Device.Connection.PacketReceive;

public interface IPacketReceiveObservable
{
	public void addObserver(String uuid, IPacketReceiveObserver o);
	public void deleteObserver(String uuid);
}
