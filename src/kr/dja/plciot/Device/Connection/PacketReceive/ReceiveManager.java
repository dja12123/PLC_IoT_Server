package kr.dja.plciot.Device.Connection.PacketReceive;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.Device.Connection.PacketProcess;

public class ReceiveManager implements IPacketReceiveObservable, IPacketReceiver
{
	private List<UDPPortReceiver> receivers;
	private Map<String, IPacketReceiveObserver> observers;
	
	public ReceiveManager(int startUDPPort, int endUDPPort)
	{
		this.receivers = new ArrayList<UDPPortReceiver>();
		this.observers = Collections.synchronizedMap(new HashMap<String, IPacketReceiveObserver>());
		
		for(int receiverPort = startUDPPort; receiverPort <= endUDPPort; ++receiverPort)
		{
			UDPPortReceiver createdReceiver;
			try
			{
				createdReceiver = new UDPPortReceiver(receiverPort, this);
			}
			catch (SocketException e)
			{
				e.printStackTrace();
				continue;
			}
			this.receivers.add(createdReceiver);
		}
	}

	@Override
	public void addObserver(String uuid, IPacketReceiveObserver o)
	{
		if(!this.observers.containsKey(uuid))
		{
			this.observers.put(uuid, o);
		}
		else
		{
			new Exception("Put Packet Observer").printStackTrace();
		}
	}

	@Override
	public void deleteObserver(String uuid)
	{
		if(this.observers.containsKey(uuid))
		{
			this.observers.remove(uuid);
		}
		else
		{
			new Exception("Remove Packet Observer").printStackTrace();
		}
	}

	@Override
	public void PacketResive(int sendPort, InetAddress sendAddress, byte[] data)
	{
		String uuid = PacketProcess.GetPacketFULLUID(data);
		
		
	}
}
