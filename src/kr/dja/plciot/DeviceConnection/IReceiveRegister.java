package kr.dja.plciot.DeviceConnection;

import kr.dja.plciot.DeviceConnection.PacketReceive.IPacketReceiveObservable;

public interface IReceiveRegister
{
	public void registerReceive(IPacketReceiveObservable observable, byte[] data);
}
