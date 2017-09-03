package kr.dja.plciot.DeviceConnection;

public interface IPacketSendCallback
{
	void packetSendCallback(boolean success, byte[] data);
}
