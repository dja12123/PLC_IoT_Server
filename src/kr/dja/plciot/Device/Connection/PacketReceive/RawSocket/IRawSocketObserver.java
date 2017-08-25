package kr.dja.plciot.Device.Connection.PacketReceive.RawSocket;

import java.net.InetAddress;

public interface IRawSocketObserver
{
	public void rawPacketResive(int sendPort, InetAddress sendAddress, byte[] data);
}
