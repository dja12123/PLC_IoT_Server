package kr.dja.plciot.DeviceConnection.PacketSend;

import java.net.InetAddress;

public interface IPacketSender
{
	 public void sendData(InetAddress sendAddress, byte[] data);
}
