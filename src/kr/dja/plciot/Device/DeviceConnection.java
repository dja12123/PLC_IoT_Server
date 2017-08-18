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

public class DeviceConnection
{
	public final byte[] macAddr;
	private final String ipAddr;
	
	private boolean connectOperation;
	private Thread taskThread;
	
	public DeviceConnection(byte[] macAddr, String ipAddr)
	{
		this.macAddr = macAddr;
		this.ipAddr = ipAddr;
		this.connectOperation = false;
		
	}
	
	public void dataSend(String name, Map<String, String> sendData, IDeviceConnectSuccess successCallback, IDeviceConnectFail failCallback)
	{

		

		
		failCallback.connectFail(this, name, sendData);
	}
	
	public void dataResive(String name, Map<String, String> sendData)
	{
		
	}
	
	public void dataCheckCallback(byte[] resiveCheckData)
	{
		
	}


}
