package kr.dja.plciot.Device.Connection.PacketReceive;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.Connection.IReceiveRegister;
import kr.dja.plciot.Device.Connection.PacketProcess;
import kr.dja.plciot.Device.Connection.PacketReceive.UDPRawSocketReceiver.UDPRawSocketThreadManage;
import kr.dja.plciot.Task.TaskLock;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class ReceiveController implements IPacketReceiveObservable, IRawSocketObserver
{
	private List<UDPRawSocketReceiver> rawSocketReceiver;
	private Map<String, IPacketReceiveObserver> observers;
	private IReceiveRegister register;
	
	private ReceiveController(IReceiveRegister register)
	{// ReceiveControllerBuilder에서 인스턴스를 생성합니다.
		this.rawSocketReceiver = new ArrayList<UDPRawSocketReceiver>();
		this.observers = Collections.synchronizedMap(new HashMap<String, IPacketReceiveObserver>());
		this.register = register;
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
			new Exception("Duplicated Put Packet Observer").printStackTrace();
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
		IPacketReceiveObserver observer = this.observers.getOrDefault(uuid, null);
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
		private List<UDPRawSocketThreadManage> receiverThreadList;
		
		public ReceiveControllerBuildManager(IReceiveRegister register)
		{
			this.instance = new ReceiveController(register);
			this.receiverThreadList = new ArrayList<UDPRawSocketThreadManage>();
		}
		
		public void createInstance(int startUDPPort, int endUDPPort, NextTask nextTask, BuildManagerCallback startCallback)
		{
			PLC_IoT_Core.CONS.push("장치 수신자 빌드 시작.");
			nextTask.insertTask((TaskOption option, NextTask insertNext)->
			{
				for(UDPRawSocketThreadManage builder : this.receiverThreadList)
				{
					this.instance.rawSocketReceiver.add(builder.getInstance());
				}
				PLC_IoT_Core.CONS.push("장치 수신자 빌드 완료.");
				startCallback.run(this.instance);
				insertNext.nextTask();
			});
			
			TaskLock totalLock = nextTask.createLock();
			for(int receiverPort = startUDPPort; receiverPort <= endUDPPort; ++receiverPort)
			{
				UDPRawSocketThreadManage builder = new UDPRawSocketThreadManage(receiverPort, this.instance, nextTask.createLock());
				this.receiverThreadList.add(builder);
			}
			totalLock.unlock();
		}
		
		public void disposeInstance(NextTask nextTask, Runnable shutdownCallback)
		{
			nextTask.insertTask((TaskOption option, NextTask insertNext)->
			{
				shutdownCallback.run();
				insertNext.nextTask();
			});
			
			TaskLock totalLock = nextTask.createLock();
			for(UDPRawSocketThreadManage builder : this.receiverThreadList)
			{
				builder.stopTask(nextTask.createLock());
			}
			totalLock.unlock();
		}
	}

	public static interface BuildManagerCallback
	{
		void run(ReceiveController instance);
	}
}

