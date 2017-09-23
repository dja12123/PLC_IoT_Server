package kr.dja.plciot.DeviceConnection.Cycle;

import java.net.InetAddress;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.DeviceConnection.PacketProcess;
import kr.dja.plciot.DeviceConnection.PacketReceive.IPacketReceiveObservable;
import kr.dja.plciot.DeviceConnection.PacketReceive.IPacketReceiveObserver;
import kr.dja.plciot.DeviceConnection.PacketSend.IPacketSender;

public class SendCycle implements Runnable, IPacketReceiveObserver
{
	private final IPacketSender sender;
	private final IPacketReceiveObservable receiver;
	private final IPacketCycleController deviceCallback;
	
	private final InetAddress addr;
	private final String uuid;
	private byte[] fullPacket;
	private byte[] packetHeader;
	private int resendCount;
	private boolean taskState;
	
	private Thread resiveTaskThread;
	
	public SendCycle(IPacketSender sender, IPacketReceiveObservable receiver,InetAddress addr
			,String macAddr, String name, String data, IPacketCycleController deviceCallback)
	{
		this.resendCount = 0;
		this.taskState = false;
		
		this.sender = sender;
		this.receiver = receiver;
		this.deviceCallback = deviceCallback;
		
		this.addr = addr;
		
		this.uuid = PacketProcess.CreateFULLUID(macAddr);
		this.packetHeader = PacketProcess.CreatePacketHeader(this.uuid);
		this.fullPacket = PacketProcess.CreateFullPacket(this.packetHeader, name, data);
		
		// 수신 메니저에 해당 사이클을 바인딩 합니다.
		this.receiver.addObserver(this.uuid, this);
		
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
		if(receivePacketSize != this.fullPacket.length)
		{
			this.errorHandling();
			return;
		}
		
		if(PacketProcess.GetPacketPhase(receivePacket) != CycleProcess.PHASE_CHECK)
		{
			this.errorHandling();
			return;
		}
		
		for(int i = 0; i < receivePacketSize; ++i)
		{
			if(receivePacket[i] != this.fullPacket[i])
			{
				if(this.resendCount > CycleProcess.MAX_RESEND)
				{
					this.errorHandling();
					return;
				}
				++this.resendCount;
				
				this.reSendPhase(this.fullPacket, CycleProcess.PHASE_START);
				return;
			}
		}
		
		this.reSendPhase(this.packetHeader, CycleProcess.PHASE_EXECUTE);
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
		
		this.taskState = false;
		this.endProcess();// 사이클 시간 제한 오류.
	}
	
	private void sendWaitTask()
	{
		this.resiveTaskThread = new Thread(this);
		this.resiveTaskThread.start();
	}
	
	private void reSendPhase(byte[] packet, byte phase)
	{// 재전송.
		PacketProcess.SetPacketPhase(packet, phase);
		this.sender.sendData(this.addr, packet);
	}
	
	private void endProcess()
	{
		String receiveName = PacketProcess.GetPacketName(this.fullPacket);
		String receiveData = PacketProcess.GetPacketData(this.fullPacket);
		
		// 장치에게 데이터 수신을 알립니다.
		this.deviceCallback.packetSendCallback(this.taskState, receiveName, receiveData);
		
		// 수신 메니저 바인딩 해제.
		this.receiver.deleteObserver(this.uuid);
	}
	
	private void errorHandling()
	{
		PLC_IoT_Core.CONS.push("Packet Send ERROR");
	}

}
