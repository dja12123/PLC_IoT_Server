package kr.dja.plciot.DeviceConnection.PacketReceive;

import java.net.InetAddress;

public interface IPacketReceiveObserver
{
	public void packetResive(byte[] resiveData);
}
