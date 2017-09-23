package kr.dja.plciot.Device;

import kr.dja.plciot.DeviceConnection.Cycle.IPacketCycleController;

public class Device implements IPacketCycleController
{
	public final String macAddr;
	private final String ipAddr;
	
	private boolean connectOperation;
	private Thread taskThread;
	
	public Device(String macAddr, String ipAddr)
	{
		this.macAddr = macAddr;
		this.ipAddr = ipAddr;
		this.connectOperation = false;
	}

	@Override
	public void packetSendCallback(boolean success, byte[] data)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void packetReceiveCallback(byte[] data)
	{
		// TODO Auto-generated method stub
		
	}



}
