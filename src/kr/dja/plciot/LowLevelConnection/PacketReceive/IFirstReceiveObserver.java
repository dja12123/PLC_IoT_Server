package kr.dja.plciot.LowLevelConnection.PacketReceive;

import java.net.InetAddress;

public interface IFirstReceiveObserver
{
	public void firstReceiveCallback(InetAddress receiveAddr, int port, byte[] data);
}
