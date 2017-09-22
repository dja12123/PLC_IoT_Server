package kr.dja.plciot.DeviceConnection.Cycle;

import java.util.Map;

import kr.dja.plciot.DeviceConnection.PacketProcess;
import kr.dja.plciot.DeviceConnection.PacketReceive.IPacketReceiveObservable;
import kr.dja.plciot.DeviceConnection.PacketReceive.IPacketReceiveObserver;
import kr.dja.plciot.DeviceConnection.PacketSend.IPacketSender;

public class ReceiveCycle implements Runnable, IPacketReceiveObserver
{
	private final IPacketSender sender;
	private final IPacketReceiveObservable receiver;
	private final IPacketCycleController deviceCallback;
	
	private final String uuid;
	private byte[] receivePacketHeader;
	private byte[] receivePacketData;
	private int resendCount;
	private boolean taskState;
	
	private Thread resiveTaskThread;
	
	public ReceiveCycle(IPacketSender sender, IPacketReceiveObservable receiver, byte[] data, IPacketCycleController deviceCallback)
	{
		this.resendCount = 0;
		this.taskState = false;
		
		this.sender = sender;
		this.receiver = receiver;
		this.deviceCallback = deviceCallback;
		
		this.ReceivePacket = data;
		this.uuid = PacketProcess.GetPacketFULLUID(data);
		
		// 수신 메니저에 해당 사이클을 바인딩 합니다.
		this.receiver.addObserver(this.uuid, this);
		
		// 발신자로부터 패킷 이 반환되어 올때까지 대기힙니다.
		this.resiveWaitTask();
		
		// 발신자에게 패킷을 반환합니다.
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
			// 전송후 인터럽트가 걸릴 때까지 대기합니다.
			// 만약 인터럽트가 걸리지 않으면 시간 초과.
			Thread.sleep(PacketProcess.TIMEOUT);
		}
		catch (InterruptedException e)
		{
			byte phase = PacketProcess.GetPacketPhase(this.ReceivePacket);
			
			if(phase == CycleProcess.PHASE_EXECUTE)
			{// 오류가 없는 실행 상태.
				this.taskState = true;
				this.endProcess();// 사이클이 정상적으로 완료되었습니다.
				return;
			}
			else if(phase == CycleProcess.PHASE_START)
			{// 오류가 있는 상태.
				if(this.resendCount < CycleProcess.MAX_RESEND)
				{// 장치에서 오류가 있다는 신호를 보낸 상태 - 재전송 필요.
					++this.resendCount;
					
					// 발신자로부터 패킷 이 반환되어 올때까지 대기힙니다.
					this.resiveWaitTask();
					// 발신자에게 패킷을 반환 합니다.
					this.reSendPhase();
					return;
				}
				else
				{// 발신자가 재대로 응답하지 않는 오류. (재전송 제한 횟수 초과)
					new Exception("Device is not responding").printStackTrace();
					this.endProcess();// task ERROR.
					return;
				}
			}
		}
		
		this.taskState = false;
		this.endProcess();// 사이클 시간 제한 오류.
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
		this.sender.sendData(sendAddress, data);
	}
	
	private void endProcess()
	{
		String receiveName = PacketProcess.GetPacketName(this.ReceivePacket);
		
		// 장치에게 데이터 수신을 알립니다.
		this.deviceCallback.ReceiveData(receiveName, receiveData, this.taskState);
		
		// 수신 메니저 바인딩 해제.
		this.receiver.deleteObserver(this.uuid);
	}
}
