package kr.dja.plciot.LowLevelConnection.PacketSend;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import kr.dja.plciot.PLC_IoT_Core;

public class SendController implements IPacketSender
{
	private final List<UDPRawSocketSender> rawSocketSender;
	
	private final Object sendDataSyncObj;
	private int beforeSendPort;
	
	public SendController(List<DatagramSocket> dataSocketList)
	{
		PLC_IoT_Core.CONS.push("로우 레벨 송신자 빌드 시작.");
		
		this.rawSocketSender = new ArrayList<UDPRawSocketSender>();
		
		for(DatagramSocket socket : dataSocketList)
		{
			this.rawSocketSender.add(new UDPRawSocketSender(socket));
		}
		this.sendDataSyncObj = new Object();
		
		PLC_IoT_Core.CONS.push("로우 레벨 송신자 빌드 완료.");
	}
	
	@Override
	public void sendData(InetAddress sendAddress, int port, byte[] data)
	{
		System.out.println("sendData1");
		synchronized(this.sendDataSyncObj)
		{
			++this.beforeSendPort;
			if(this.beforeSendPort >= this.rawSocketSender.size())
			{
				this.beforeSendPort = 0;
			}
		}
		this.rawSocketSender.get(port).sendData(sendAddress, port, data);
		System.out.println("sendData2");
	}
}
