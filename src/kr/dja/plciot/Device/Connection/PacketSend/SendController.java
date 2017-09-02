package kr.dja.plciot.Device.Connection.PacketSend;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.Connection.PacketProcess;

public class SendController implements IDataSender
{
	private final List<UDPRawSocketSender> rawSocketSender;
	
	private final Object sendDataSyncObj;
	private int beforeSendPort;
	
	public SendController(List<DatagramSocket> dataSocketList)
	{
		PLC_IoT_Core.CONS.push("장치 송신자 빌드 시작.");
		
		this.rawSocketSender = new ArrayList<UDPRawSocketSender>();
		
		for(DatagramSocket socket : dataSocketList)
		{
			this.rawSocketSender.add(new UDPRawSocketSender(socket));
		}
		this.sendDataSyncObj = new Object();
		
		PLC_IoT_Core.CONS.push("장치 송신자 빌드 완료.");
	}
	
	@Override
	public void sendData(InetAddress sendAddress, byte[] data)
	{
		synchronized(this.sendDataSyncObj)
		{
			++this.beforeSendPort;
			if(this.beforeSendPort >= this.rawSocketSender.size())
			{
				this.beforeSendPort = 0;
			}
		}
		this.rawSocketSender.get(this.beforeSendPort).sendData(sendAddress, data);
	}
}
