package kr.dja.plciot.Device.Connection.PacketSend;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.Connection.PacketProcess;

public class SendController
{
	private final int startPort;
	private final int endPort;
	private Map<Integer, UDPRawSocketSender> rawSocketSender;
	
	private int beforeSendPort;
	
	public SendController(int startPort, int endPort)
	{
		PLC_IoT_Core.CONS.push("장치 송신자 빌드 시작.");
		
		this.startPort = startPort;
		this.endPort = endPort;
		this.rawSocketSender = new HashMap<Integer, UDPRawSocketSender>();
		
		for(int i = this.startPort; i <= this.endPort; ++i)
		{
			try
			{
				DatagramSocket sndSocket = new DatagramSocket(i);
				this.rawSocketSender.put(i, new UDPRawSocketSender(sndSocket));
			}
			catch (SocketException e)
			{
				e.printStackTrace();
			}
		}
		this.beforeSendPort = this.startPort;
		
		PLC_IoT_Core.CONS.push("장치 송신자 빌드 완료.");
	}
	
	public void sendData(InetAddress sendAddress, byte[] data)
	{
		++this.beforeSendPort;
		if(this.beforeSendPort > this.endPort)
		{
			this.beforeSendPort = this.startPort;
		}
		
		this.rawSocketSender.get(this.beforeSendPort).sendData(sendAddress, data);
	}
}
