package kr.dja.plciot.Device.Connection;

import kr.dja.plciot.Device.Connection.PacketReceive.IPacketReceiveObservable;

public interface IReceiveRegister
{
	public void registerReceive(IPacketReceiveObservable observable, byte[] data);
}
