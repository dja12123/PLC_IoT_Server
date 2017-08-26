package kr.dja.plciot.Device;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.Device.Connection.PacketReceive.IPacketReceiveObservable;

public class Device implements INetworkManager, IDevicePacketReceiveObserver
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
	public void sendData(String name, Map<String, String> sendData)
	{
		
	}

	@Override
	public void addReceiveObserver(String name, IDevicePacketReceiveObserver observer)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ReceiveData(String name, Map<String, String> data, boolean success)
	{
		// TODO Auto-generated method stub
		
	}


}
