package kr.dja.plciot.Device.Connection.PacketSend;

import java.net.InetAddress;

public interface IDataSender
{
	 public void sendData(InetAddress sendAddress, byte[] data);
}
