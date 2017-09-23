package kr.dja.plciot.DeviceConnection.PacketReceive;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.DeviceConnection.IReceiveRegister;
import kr.dja.plciot.DeviceConnection.PacketProcess;
import kr.dja.plciot.DeviceConnection.PacketReceive.UDPRawSocketReceiver.UDPRawSocketThreadManage;
import kr.dja.plciot.Task.TaskLock;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class ReceiveController implements IPacketReceiveObservable, IRawSocketObserver
{
	private final List<UDPRawSocketReceiver> rawSocketReceiver;
	private final Map<String, IPacketReceiveObserver> observers;
	private final IReceiveRegister register;
	
	public ReceiveController(IReceiveRegister register)
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
	public void rawPacketResive(int sendPort, InetAddress receiveAddr, byte[] data)
	{
		String uuid = PacketProcess.GetPacketFULLUID(data);
		PLC_IoT_Core.CONS.push(uuid);
		IPacketReceiveObserver observer = this.observers.getOrDefault(uuid, null);
		if(observer != null)
		{
			observer.packetResive(data);
		}
		else
		{
			this.register.registerReceive(receiveAddr, data);
		}
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
			PLC_IoT_Core.CONS.push("장치 수신자 빌드 시작.");
			nextTask.insertTask((TaskOption option, NextTask insertNext)->
			{
				for(UDPRawSocketThreadManage builder : this.receiverThreadList)
				{
					this.instance.rawSocketReceiver.add(builder.getInstance());
				}
				PLC_IoT_Core.CONS.push("장치 수신자 빌드 완료.");
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

