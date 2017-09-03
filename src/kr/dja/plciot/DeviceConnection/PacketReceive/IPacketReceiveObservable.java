package kr.dja.plciot.DeviceConnection.PacketReceive;

public interface IPacketReceiveObservable
{
	public void addObserver(String uuid, IPacketReceiveObserver o);
	public void deleteObserver(String uuid);
}
