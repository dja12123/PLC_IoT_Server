package kr.dja.plciot.DeviceConnection.PacketReceive;

import java.net.InetAddress;

public interface IRawSocketObserver
{
	public void rawPacketResive(int sendPort, InetAddress sendAddress, byte[] data);
}
