package kr.dja.plciot.LowLevelConnection.Cycle;

import java.net.InetAddress;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.LowLevelConnection.PacketProcess;
import kr.dja.plciot.LowLevelConnection.PacketReceive.IPacketReceiveObservable;
import kr.dja.plciot.LowLevelConnection.PacketSend.IPacketSender;

public class SendCycle extends AbsCycle implements Runnable
{
	private final int port;
	private final String macAddr;
	private final String name;
	private final String data;
	private byte[] fullPacket;
	private byte[] packetHeader;
	private int resendCount;
	private boolean taskState;
	
	private Thread resiveTaskThread;
	
	private SendCycle(IPacketSender sender, IPacketReceiveObservable receiver, InetAddress addr, int port
			,String macAddr, String name, String data, IPacketCycleUser user, IEndCycleCallback endCycleCallback)
	{
		super(sender, receiver, addr, endCycleCallback, user);
		
		this.port = port;
		this.macAddr = macAddr;
		this.name = name;
		this.data = data;
		
		this.resendCount = 0;
		this.taskState = false;
	}
	
	@Override
	public void start()
	{
		String fullUID = PacketProcess.CreateFULLUID(this.macAddr);
		super.startTask(fullUID);
		
		this.packetHeader = PacketProcess.CreatePacketHeader(fullUID);
		this.fullPacket = PacketProcess.CreateFullPacket(this.packetHeader, this.name, this.data);
		// 발신자로부터 패킷 이 반환되어 올때까지 대기힙니다.
		this.sendWaitTask();
		
		// 패킷을 전송합니다.
		this.reSendPhase(this.fullPacket, CycleProcess.PHASE_START);
	}
	
	@Override
	public synchronized void packetReceive(byte[] receivePacket)
	{// 패킷을 받은 상태일때 패킷을 검사.
		
		this.resiveTaskThread.interrupt();
		int receivePacketSize = PacketProcess.GetPacketSize(receivePacket);
		
		System.out.println("SendCycle에서 수신:");
		PacketProcess.PrintDataPacket(receivePacket);
		
		if(PacketProcess.GetPacketPhase(receivePacket) == CycleProcess.PHASE_EXECUTE)
		{
			System.out.println("수신 완료.");
			this.taskState = true;
			this.endProcess();
			return;
		}
		
		
		if(receivePacketSize != this.fullPacket.length)
		{
			this.errorHandling("Packet length error.");
			return;
		}
		
		if(PacketProcess.GetPacketPhase(receivePacket) != CycleProcess.PHASE_CHECK)
		{
			this.errorHandling("Phase is not PHASE_CHECK.");
			return;
		}
		
		if(!PacketProcess.ComparePacket(receivePacket, this.fullPacket))
		{
			if(this.resendCount >= CycleProcess.MAX_RESEND)
			{
				this.errorHandling("Too many resend error.");
				return;
			}
			++this.resendCount;
			
			// 발신자로부터 패킷 이 반환되어 올때까지 대기힙니다.
			this.sendWaitTask();
			this.reSendPhase(this.fullPacket, CycleProcess.PHASE_START);
			
			return;
		}

		this.reSendPhase(this.packetHeader, CycleProcess.PHASE_EXECUTE);
		return;
	}
	
	@Override
	public void run()
	{
		synchronized(this)
		{
			this.notify();
		}
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
		if(this.resendCount >= CycleProcess.MAX_RESEND)
		{
			this.errorHandling("Device is not responding.");
			return;
		}
		++this.resendCount;
		System.out.println("resend(" + this.resendCount +")");
		
		// 발신자로부터 패킷 이 반환되어 올때까지 대기힙니다.
		this.sendWaitTask();
		this.reSendPhase(this.fullPacket, CycleProcess.PHASE_START);
		
	}
	
	synchronized private void sendWaitTask()
	{
		this.resiveTaskThread = new Thread(this);
		this.resiveTaskThread.start();
		try
		{
			this.wait();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	private void reSendPhase(byte[] packet, byte phase)
	{// 재전송.
		PacketProcess.SetPacketPhase(packet, phase);
		this.sender.sendData(this.addr, this.port, packet);
		System.out.println("SendCycle에서 송신:" + phase + " " + this.port);
		PacketProcess.PrintDataPacket(packet);
	}
	
	private void endProcess()
	{
		this.notifyEndCycle();
		
		String receiveName = null;
		String receiveData = null;
		
		if(this.taskState)
		{
			receiveName = PacketProcess.GetPacketName(this.fullPacket);
			receiveData = PacketProcess.GetPacketData(this.fullPacket);
		}
		
		// 장치에게 데이터 송신이 완료되었음을 알립니다.
		this.user.packetSendCallback(this.taskState, receiveName, receiveData);
	}
	
	private void errorHandling(String str)
	{
		new Exception(str).printStackTrace();
		PLC_IoT_Core.CONS.push("Packet Send ERROR " + str);
		this.endProcess();
	}
	
	public static class SendCycleBuilder extends AbsCycleBuilder
	{
		private String name;
		private String data;
		private String macAddr;
		private int port;
		
		public SendCycleBuilder(){}
		
		
		public SendCycleBuilder setPacketName(String name)
		{
			this.name = name;
			return this;
		}
		
		public SendCycleBuilder setPacketData(String data)
		{
			this.data = data;
			return this;
		}
		
		public SendCycleBuilder setPacketMacAddr(String macAddr)
		{
			this.macAddr = macAddr;
			return this;
		}
		
		public SendCycleBuilder setPacketPort(int port)
		{
			this.port = port;
			return this;
		}
		
		public SendCycle getInstance()
		{
			return new SendCycle(sender, receiver, addr, port, macAddr, name, data, user, endCycleCallback);
		}
	}
}
