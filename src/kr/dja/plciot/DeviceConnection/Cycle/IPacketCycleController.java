package kr.dja.plciot.DeviceConnection.Cycle;

public interface IPacketCycleController
{
	void packetSendCallback(boolean success, String name, String data);
	void packetReceiveCallback(String name, String data);
}