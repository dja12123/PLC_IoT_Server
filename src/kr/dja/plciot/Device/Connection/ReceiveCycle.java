package kr.dja.plciot.Device.Connection;

import kr.dja.plciot.Device.Connection.PacketReceive.IPacketReceiveObservable;
import kr.dja.plciot.Device.Connection.PacketReceive.IPacketReceiveObserver;

public class ReceiveCycle implements Runnable, IPacketReceiveObserver
{
	private final IPacketReceiveObservable observable;
	
	private final String uuid;
	private byte[] ReceivePacket;
	private int resendCount;
	private boolean taskState;
	
	private Thread resiveTaskThread;
	
	public ReceiveCycle(IPacketReceiveObservable observable, byte[] data)
	{
		this.resendCount = 0;
		this.taskState = false;
		this.observable = observable;
		this.ReceivePacket = data;
		this.uuid = PacketProcess.GetPacketFULLUID(data);
		this.observable.addObserver(this.uuid, this);
		
		this.resiveWaitTask();
		this.reSendPhase();
	}
	
	@Override
	public void packetResive(byte[] resivePacket)
	{// 패킷을 받은 상태일때.
		this.ReceivePacket = resivePacket;
		this.resiveTaskThread.interrupt();
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
			//OK
			if(PacketProcess.GetPacketPhase(this.ReceivePacket) == PacketProcess.PHASE_EXECUTE)
			{// 오류가 없는 실행 상태.
				this.taskState = true;
				this.endProcess();// task OK.
				return;
			}
			else if(PacketProcess.GetPacketPhase(this.ReceivePacket) == PacketProcess.PHASE_SEND)
			{// 오류가 있는 상태.
				if(this.resendCount < PacketProcess.MAX_RESEND)
				{
					++this.resendCount;
					this.resiveWaitTask();
					this.reSendPhase();
					return;
				}
				else
				{
					
					this.endProcess();// task ERROR.
				}
			}
		}
		
		this.taskState = false;
		this.endProcess();// timeout ERROR.
	}
	
	public boolean getResiveState()
	{
		return this.taskState;
	}
	
	private void resiveWaitTask()
	{
		this.resiveTaskThread = new Thread(this);
		this.resiveTaskThread.start();
	}
	
	private void reSendPhase()
	{// 재전송.
		
	}
	
	private void endProcess()
	{
		this.observable.deleteObserver(this.uuid);
	}
}
