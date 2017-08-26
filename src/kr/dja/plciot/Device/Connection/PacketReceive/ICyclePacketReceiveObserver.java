package kr.dja.plciot.Device.Connection.PacketReceive;

public interface ICyclePacketReceiveObserver
{
	public void packetResive(byte[] resiveData);
}
