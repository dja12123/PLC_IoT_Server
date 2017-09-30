package kr.dja.plciot.LowLevelConnection.PacketSend;

import java.net.InetAddress;

public interface IPacketSender
{
	 public void sendData(InetAddress sendAddress, int port, byte[] data);
}
