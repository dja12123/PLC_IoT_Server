package kr.dja.plciot.LowLevelConnection.Cycle;

public interface IPacketCycleUser
{
	void packetSendCallback(boolean success, String name, String data);
	void packetReceiveCallback(String name, String data);
}