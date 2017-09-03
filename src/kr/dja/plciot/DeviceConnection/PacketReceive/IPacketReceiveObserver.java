package kr.dja.plciot.DeviceConnection.PacketReceive;

public interface IPacketReceiveObserver
{
	public void packetResive(byte[] resiveData);
}
