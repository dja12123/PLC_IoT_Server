package kr.dja.plciot.DeviceConnection.Cycle;

public interface IPacketCycleController
{
	void packetSendCallback(boolean success, byte[] data);
	void packetReceiveCallback(byte[] data);
}