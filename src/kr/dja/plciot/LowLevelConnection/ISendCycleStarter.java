package kr.dja.plciot.LowLevelConnection;

import java.net.InetAddress;

import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;

public interface ISendCycleStarter
{
	public void startSendCycle(InetAddress addr, int port
			,String macAddr, String name, String data, IPacketCycleUser target);
}
