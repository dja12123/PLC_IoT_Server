package kr.dja.plciot.LowLevelConnection.Cycle;

import java.net.InetAddress;
import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.LowLevelConnection.PacketProcess;
import kr.dja.plciot.LowLevelConnection.PacketReceive.IPacketReceiveObservable;
import kr.dja.plciot.LowLevelConnection.PacketSend.IPacketSender;

public class ReceiveCycle extends AbsCycle implements Runnable
{	
	private byte[] receivePacket;
	private int resendCount;
	
	private Thread resiveTaskThread;
	
	private ReceiveCycle(IPacketSender sender, IPacketReceiveObservable receiver, InetAddress addr
			,int port, byte[] data, IPacketCycleUser userCallback, IEndCycleCallback endCycleCallback)
	{
		super(sender, receiver, addr, port, endCycleCallback, userCallback);
		this.resendCount = 0;
		this.receivePacket = data;
	}
	
	@Override
	public void start()
	{
		this.startTask(PacketProcess.GetPacketFULLUID(this.receivePacket));
		// 발신자로부터 패킷 이 반환되어 올때까지 대기힙니다.
		this.resiveWaitTask();
		
		// 발신자에게 패킷을 반환합니다.
		this.reSendPhase(CycleProcess.PHASE_CHECK);
	}

	@Override
	public synchronized void packetReceive(byte[] resiveData)
	{
		this.receivePacket = resiveData;
		this.resiveTaskThread.interrupt();
		
		byte phase = PacketProcess.GetPacketPhase(this.receivePacket);
		
		if(phase == CycleProcess.PHASE_EXECUTE)
		{// 오류가 없는 실행 상태.
			this.endProcess();// 사이클이 정상적으로 완료되었습니다.
			return;
		}
		else if(phase == CycleProcess.PHASE_START)
		{// 오류가 있는 상태.
			if(this.resendCount > CycleProcess.MAX_RESEND)
			{// 장치에서 오류가 있다는 신호를 보낸 상태 - 재전송 필요.
				
				this.errorHandling("Too many resend error.");
				return;
			}
			++this.resendCount;
			
			// 발신자로부터 패킷 이 반환되어 올때까지 대기힙니다.
			this.resiveWaitTask();
			// 발신자에게 패킷을 반환 합니다.
			this.reSendPhase(CycleProcess.PHASE_CHECK);
			return;
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			// 전송후 인터럽트가 걸릴 때까지 대기합니다.
			// 만약 인터럽트가 걸리지 않으면 시간 초과.
			Thread.sleep(CycleProcess.TIMEOUT);
		}
		catch (InterruptedException e)
		{
			return;
		}
		
		this.errorHandling("Device is not responding.");
	}

	private void resiveWaitTask()
	{
		this.resiveTaskThread = new Thread(this);
		this.resiveTaskThread.start();
	}
	
	private void reSendPhase(byte phase)
	{// 재전송.
		PacketProcess.SetPacketPhase(this.receivePacket, phase);
		this.sender.sendData(this.addr, this.port, this.receivePacket);
	}
	
	private void endProcess()
	{
		this.notifyEndCycle();
		
		String receiveName = PacketProcess.GetPacketName(this.receivePacket);
		String receiveData = PacketProcess.GetPacketData(this.receivePacket);
		
		// 장치에게 데이터 수신을 알립니다.
		this.user.packetReceiveCallback(receiveName, receiveData);
		
		// 수신 메니저 바인딩 해제.
		
	}
	
	private void errorHandling(String str)
	{
		new Exception(str).printStackTrace();
		PLC_IoT_Core.CONS.push("Packet Send ERROR " + str);
		this.endProcess();
	}
	
	public static class ReceiveCycleBuilder extends AbsCycleBuilder
	{
		private byte[] data;
		
		public ReceiveCycleBuilder(){}
		
		public ReceiveCycleBuilder setPacketData(byte[] data)
		{
			this.data = data;
			return this;
		}
		
		
		public ReceiveCycle getInstance()
		{
			return new ReceiveCycle(sender, receiver, addr, port, data, user, endCycleCallback);
		}
	}
}
