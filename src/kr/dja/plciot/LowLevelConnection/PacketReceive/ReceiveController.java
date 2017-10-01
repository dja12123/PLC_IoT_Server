package kr.dja.plciot.LowLevelConnection.PacketReceive;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.LowLevelConnection.PacketProcess;
import kr.dja.plciot.LowLevelConnection.PacketReceive.UDPRawSocketReceiver.UDPRawSocketThreadManage;
import kr.dja.plciot.Task.TaskLock;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class ReceiveController implements IPacketReceiveObservable, IRawSocketObserver
{
	private final List<UDPRawSocketReceiver> rawSocketReceiver;
	private final Map<String, IPacketReceiveObserver> observers;
	private final IFirstReceiveObserver register;
	private final ExecutorService threadPool;
	
	public ReceiveController(IFirstReceiveObserver register)
	{// ReceiveControllerBuilder에서 인스턴스를 생성합니다.
		this.rawSocketReceiver = new ArrayList<UDPRawSocketReceiver>();
		this.observers = Collections.synchronizedMap(new HashMap<String, IPacketReceiveObserver>());
		this.register = register;
		this.threadPool = Executors.newCachedThreadPool();
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
	public void rawPacketResive(int sendPort, InetAddress receiveAddr, byte[] data)
	{
		this.threadPool.submit(()->
		{
			if(!PacketProcess.CheckPacket(data))
			{
				return;
			}
			
			String uuid = PacketProcess.GetPacketFULLUID(data);
			IPacketReceiveObserver observer = this.observers.getOrDefault(uuid, null);
			if(observer != null)
			{
				observer.packetReceive(data);
			}
			else
			{
				this.register.firstReceiveCallback(receiveAddr, sendPort, data);
			}
		});
	}
	
	public static class ReceiveControllerBuildManager implements IMultiThreadTaskCallback
	{
		private final ReceiveController instance;
		private final List<UDPRawSocketThreadManage> receiverThreadList;
		private List<DatagramSocket> dataSocketList;
		
		public ReceiveControllerBuildManager(ReceiveController instance)
		{
			this.instance = instance;
			this.receiverThreadList = new ArrayList<UDPRawSocketThreadManage>();
			this.dataSocketList = new ArrayList<DatagramSocket>();
		}
		
		public void setDatagramSocket(List<DatagramSocket> dataSocketList)
		{
			this.dataSocketList = dataSocketList;
		}
		
		private void initializeValues(NextTask nextTask)
		{
			PLC_IoT_Core.CONS.push("로우 레벨 수신자 빌드 시작.");
			nextTask.insertTask((TaskOption option, NextTask insertNext)->
			{
				for(UDPRawSocketThreadManage builder : this.receiverThreadList)
				{
					this.instance.rawSocketReceiver.add(builder.getInstance());
				}
				PLC_IoT_Core.CONS.push("로우 레벨 수신자 빌드 완료.");
				insertNext.nextTask();
			});
			
			TaskLock totalLock = nextTask.createLock();
			for(DatagramSocket socket : this.dataSocketList)
			{
				UDPRawSocketThreadManage builder = new UDPRawSocketThreadManage(socket, this.instance, nextTask.createLock());
				this.receiverThreadList.add(builder);
			}
			totalLock.unlock();
		}
		
		private void disposeInstance(NextTask nextTask)
		{
			TaskLock totalLock = nextTask.createLock();
			for(UDPRawSocketThreadManage builder : this.receiverThreadList)
			{
				builder.stopTask(nextTask.createLock());
			}
			
			totalLock.unlock();
		}

		@Override
		public void executeTask(TaskOption option, NextTask nextTask)
		{
			if(option == TaskOption.START)
			{
				this.initializeValues(nextTask);
			}
			else if(option == TaskOption.SHUTDOWN)
			{
				this.disposeInstance(nextTask);
			}
			
		}
	}
}

