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

public class DeviceConnection implements Runnable
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
		this.connectOperation = true;
		this.taskThread = new Thread(this);
		this.taskThread.start();
		
		byte[] sendDataByte = new byte[PacketProcess.DATAPACKET_TOTAL_BUFFER];
		
		//DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
		
		failCallback.connectFail(this, name, sendData);
	}
	
	public void dataCheckCallback(byte[] resiveCheckData)
	{
		
	}

	@Override
	public void run()
	{
		try
		{
			Thread.sleep(PacketProcess.TIMEOUT);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
	}
	

	
}
