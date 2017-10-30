package kr.dja.plciot.LowLevelConnection.Cycle;

import java.net.InetAddress;

public interface IPacketCycleUser
{
	void packetSendCallback(boolean success, String name, String data);
	void packetReceiveCallback(InetAddress ipAddr, String macAddr, String name, String data);
}