package kr.dja.plciot.Device.Connection.PacketReceive;

import java.net.InetAddress;

public interface IPacketReceiver
{
	public void PacketResive(int sendPort, InetAddress sendAddress, byte[] data);
}
