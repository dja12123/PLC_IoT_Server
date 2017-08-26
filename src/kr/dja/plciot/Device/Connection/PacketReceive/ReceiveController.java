package kr.dja.plciot.Device.Connection.PacketReceive;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.Device.Connection.IReceiveRegister;
import kr.dja.plciot.Device.Connection.PacketProcess;
import kr.dja.plciot.Device.Connection.PacketReceive.RawSocket.IRawSocketObserver;
import kr.dja.plciot.Device.Connection.PacketReceive.RawSocket.UDPRawSocketReceiver;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class ReceiveController implements IPacketReceiveObservable, IRawSocketObserver, IMultiThreadTaskCallback
{
	private List<UDPRawSocketReceiver> rawSocketReceiver;
	private Map<String, ICyclePacketReceiveObserver> observers;
	private IReceiveRegister communicator;
	
	public ReceiveController(int startUDPPort, int endUDPPort, IReceiveRegister communicator)
	{
		this.rawSocketReceiver = new ArrayList<UDPRawSocketReceiver>();
		this.observers = Collections.synchronizedMap(new HashMap<String, ICyclePacketReceiveObserver>());
		this.communicator = communicator;
		
		for(int receiverPort = startUDPPort; receiverPort <= endUDPPort; ++receiverPort)
		{
			UDPRawSocketReceiver createdReceiver;
			try
			{
				createdReceiver = new UDPRawSocketReceiver(receiverPort, this);
			}
			catch (SocketException e)
			{
				e.printStackTrace();
				continue;
			}
			this.rawSocketReceiver.add(createdReceiver);
		}
	}

	@Override
	public void addObserver(String uuid, ICyclePacketReceiveObserver o)
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
	public void rawPacketResive(int sendPort, InetAddress sendAddress, byte[] data)
	{
		String uuid = PacketProcess.GetPacketFULLUID(data);
		ICyclePacketReceiveObserver observer = this.observers.getOrDefault(uuid, null);
		if(observer != null)
		{
			observer.packetResive(data);
		}
		else
		{
			this.communicator.registerReceive(this, data);
		}
	}

	@Override
	public void executeTask(TaskOption option, NextTask nextTask)
	{
		if(option == TaskOption.START)
		{
			for(UDPRawSocketReceiver receiver : this.rawSocketReceiver)
			{
				new Thread(receiver).start();
			}
			nextTask.nextTask();
		}
		else if(option == TaskOption.SHUTDOWN)
		{
			
		}
		
	}
}
