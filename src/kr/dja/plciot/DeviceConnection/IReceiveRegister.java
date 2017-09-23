package kr.dja.plciot.DeviceConnection;

import java.net.InetAddress;

public interface IReceiveRegister
{
	public void registerReceive(InetAddress addr, byte[] data);
}
