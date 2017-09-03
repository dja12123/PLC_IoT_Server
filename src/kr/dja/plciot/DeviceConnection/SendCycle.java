package kr.dja.plciot.DeviceConnection;

import kr.dja.plciot.DeviceConnection.PacketReceive.IPacketReceiveObservable;
import kr.dja.plciot.DeviceConnection.PacketReceive.IPacketReceiveObserver;
import kr.dja.plciot.DeviceConnection.PacketSend.IPacketSender;

public class SendCycle implements Runnable, IPacketReceiveObserver
{
	public SendCycle(IPacketSender sender, IPacketReceiveObservable receiver, byte[] data, IPacketSendCallback sendCallback)
	{
		
	}
	
	@Override
	public void run()
	{
		
		
	}

	@Override
	public void packetResive(byte[] resiveData)
	{
		
		
	}

}
