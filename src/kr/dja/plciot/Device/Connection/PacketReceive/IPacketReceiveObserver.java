package kr.dja.plciot.Device.Connection.PacketReceive;

public interface IPacketReceiveObserver
{
	public void packetResive(byte[] resiveData);
}
