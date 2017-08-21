package kr.dja.plciot.Device.Connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class DeviceConnect implements INetworkManager
{
	public final byte[] macAddr;
	private final String ipAddr;
	
	private boolean connectOperation;
	private Thread taskThread;
	
	public DeviceConnect(byte[] macAddr, String ipAddr)
	{
		this.macAddr = macAddr;
		this.ipAddr = ipAddr;
		this.connectOperation = false;
		
	}
	
	@Override
	public void sendData(String name, Map<String, String> sendData, IDeviceConnectSuccessCallback successCallback,
			IDeviceConnectFailCallback failCallback)
	{
		
		
	}

	@Override
	public void receiveData(String name, Map<String, String> sendData)
	{
		
		
	}


}
