package kr.dja.plciot.Device.Connection.PacketReceive;

public interface IPacketReceiveObservable
{
	public void addObserver(byte[] uuid, IPacketReceiveObserver o);
	public void deleteObserver(byte[] uuid);
}
