package kr.dja.plciot.LowLevelConnection.PacketReceive;

public interface IPacketReceiveObserver
{
	public void packetReceive(byte[] receiveData);
}
