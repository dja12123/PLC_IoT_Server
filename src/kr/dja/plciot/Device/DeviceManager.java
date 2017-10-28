package kr.dja.plciot.Device;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Database.DatabaseConnector;
import kr.dja.plciot.LowLevelConnection.ConnectionManager;
import kr.dja.plciot.LowLevelConnection.INewConnectionHandler;
import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class DeviceManager implements INewConnectionHandler, IPacketCycleUser, IMultiThreadTaskCallback
{
	private static final String DEVICE_REGISTER = "register";
	private final ConnectionManager cycleManager;
	private final DatabaseConnector dbConnector;
	private final Map<String, Device> deviceList;
	
	public DeviceManager(ConnectionManager connectionManager, DatabaseConnector dbConnector)
	{
		this.cycleManager = connectionManager;
		this.deviceList = new HashMap<String, Device>();
		this.dbConnector = dbConnector;
	}
	
	@Override
	public IPacketCycleUser createConnection(String uuid, String name)
	{// 장치 ID 넘어옴
		Device receiveTarget = this.deviceList.getOrDefault(uuid, null);
		if(receiveTarget != null)
		{
			return receiveTarget;
		}
		if(name.equals(DEVICE_REGISTER))
		{
			PLC_IoT_Core.CONS.push("장치 등록 시도.");
			return this;
		}
		
		return null;
	}
	
	@Override
	public void packetSendCallback(boolean success, String name, String data)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void packetReceiveCallback(String name, String data)
	{
		// 장치 등록 사이클 시작
		
	}
	
	private void start(NextTask nextTask)
	{
		PLC_IoT_Core.CONS.push("장치 관리자 빌드 시작.");
		this.cycleManager.addReceiveHandler(this);
		
		ResultSet deviceList = this.dbConnector.sqlQuery("select * from device");
		deviceList.
		
		PLC_IoT_Core.CONS.push("장치 관리자 빌드 완료.");
		nextTask.nextTask();
	}
	
	private void shutdown(NextTask nextTask)
	{
		PLC_IoT_Core.CONS.push("장치 관리자 종료 시작.");
		this.cycleManager.removeReceiveHandler(this);
		PLC_IoT_Core.CONS.push("장치 관리자 빌드 성공.");
		nextTask.nextTask();
	}

	@Override
	public void executeTask(TaskOption option, NextTask nextTask)
	{
		if(option == TaskOption.START)
		{
			this.start(nextTask);
		}
		else if(option == TaskOption.SHUTDOWN)
		{
			this.shutdown(nextTask);
		}
	}
}
