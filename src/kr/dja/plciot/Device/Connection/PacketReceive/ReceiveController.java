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
import kr.dja.plciot.Device.Connection.PacketReceive.UDPRawSocketReceiver.UDPRawSocketThread;
import kr.dja.plciot.Task.TaskLock;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class ReceiveController implements IPacketReceiveObservable, IRawSocketObserver
{
	private List<UDPRawSocketReceiver> rawSocketReceiver;
	private Map<String, ICyclePacketReceiveObserver> observers;
	private IReceiveRegister register;
	
	private ReceiveController(IReceiveRegister register)
	{// ReceiveControllerBuilder에서 인스턴스를 생성합니다.
		this.rawSocketReceiver = new ArrayList<UDPRawSocketReceiver>();
		this.observers = Collections.synchronizedMap(new HashMap<String, ICyclePacketReceiveObserver>());
		this.register = register;
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
			this.register.registerReceive(this, data);
		}
	}
	
	public static class ReceiveControllerBuildManager
	{
		private ReceiveController instance;
		private List<UDPRawSocketThread> receiverThreadList;
		
		public ReceiveControllerBuildManager(IReceiveRegister register)
		{
			this.instance = new ReceiveController(register);
			this.receiverThreadList = new ArrayList<UDPRawSocketThread>();
		}
		
		public void createInstance(int startUDPPort, int endUDPPort, NextTask nextTask, BuildManagerCallback startCallback)
		{
			nextTask.insertTask((TaskOption option, NextTask insertNext)->
			{
				for(UDPRawSocketThread builder : this.receiverThreadList)
				{
					this.instance.rawSocketReceiver.add(builder.getInstance());
				}
				
				startCallback.callback(this.instance);
				insertNext.nextTask();
			});
			
			TaskLock totalLock = nextTask.createLock();
			for(int receiverPort = startUDPPort; receiverPort <= endUDPPort; ++receiverPort)
			{
				UDPRawSocketThread builder = new UDPRawSocketThread(receiverPort, this.instance, nextTask.createLock());
				this.receiverThreadList.add(builder);
			}
			totalLock.unlock();
		}
		
		public void disposeInstance(NextTask nextTask, BuildManagerCallback shutdownCallback)
		{
			nextTask.insertTask((TaskOption option, NextTask insertNext)->
			{
				shutdownCallback.callback(this.instance);
				insertNext.nextTask();
			});
			
			TaskLock totalLock = nextTask.createLock();
			for(UDPRawSocketThread builder : this.receiverThreadList)
			{
				builder.stopTask(nextTask.createLock());
			}
			totalLock.unlock();
		}
	}
}

interface BuildManagerCallback
{
	void callback(ReceiveController instance);
}
