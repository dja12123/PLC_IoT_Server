package kr.dja.plciot.Device;

import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;

public abstract class Device implements IPacketCycleUser
{
	public final String macAddr;
	
	private boolean isConnected;
	
	public Device(String macAddr)
	{
		this.macAddr = macAddr;
		this.isConnected = false;
	}

	@Override
	public abstract void packetSendCallback(boolean success, String name, String data);

	@Override
	public abstract void packetReceiveCallback(String name, String data);



}
