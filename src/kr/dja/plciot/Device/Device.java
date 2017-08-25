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

import kr.dja.plciot.Device.Connection.IDeviceCommunicationCallback;
import kr.dja.plciot.Device.Connection.PacketReceive.IPacketReceiveObservable;

public class Device implements INetworkManager, 
{
	public final byte[] macAddr;
	private final String ipAddr;
	
	private boolean connectOperation;
	private Thread taskThread;
	
	public Device(byte[] macAddr, String ipAddr)
	{
		this.macAddr = macAddr;
		this.ipAddr = ipAddr;
		this.connectOperation = false;
		
	}
	
	@Override
	public void sendData(String name, Map<String, String> sendData, IDeviceCommunicationCallback failCallback)
	{
		
	}

	@Override
	public void addReceiveObserver(String name, IReceiveObserver observer)
	{
		// TODO Auto-generated method stub
		
	}
}
