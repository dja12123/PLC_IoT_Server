package kr.dja.plciot.Device;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Database.DatabaseConnector;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.AbsDevice.DataFlow.DeviceConsent;
import kr.dja.plciot.Device.TaskManager.DeviceValueDBStore;
import kr.dja.plciot.LowLevelConnection.ConnectionManager;
import kr.dja.plciot.LowLevelConnection.INewConnectionHandler;
import kr.dja.plciot.LowLevelConnection.PacketProcess;
import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class DeviceManager implements INewConnectionHandler, IPacketCycleUser, IMultiThreadTaskCallback
{
	private static final String DEVICE_REGISTER = "register";
	
	private final ConnectionManager cycleManager;
	private final DatabaseConnector dbConnector;
	private final Map<String, AbsDevice> deviceList;
	
	private final DeviceValueDBStore dbStoreHandler;
		
	public DeviceManager(ConnectionManager connectionManager, DatabaseConnector dbConnector)
	{
		this.cycleManager = connectionManager;
		this.deviceList = new HashMap<String, AbsDevice>();
		this.dbConnector = dbConnector;
		
		this.dbStoreHandler = new DeviceValueDBStore();
	}
	
	@Override
	public IPacketCycleUser createConnection(String uuid, String name)
	{// 장치 ID 넘어옴
		AbsDevice receiveTarget = this.deviceList.getOrDefault(uuid, null);
		if(receiveTarget != null)
		{
			return receiveTarget;
		}
		if(name.equals(DEVICE_REGISTER))
		{
			PLC_IoT_Core.CONS.push("장치 등록 시도.");
			return this;
		}
		
		return this;
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
		if(name.equals(DEVICE_REGISTER))
		{
			PLC_IoT_Core.CONS.push("장치 등록.");
			String[] splitData = data.split(PacketProcess.DEFAULT_SPLIT_REGEX);
			String deviceUUID = splitData[0];// UUID
			String deviceType = splitData[1];// DEVICE TYPE
			String ipAddr = splitData[2];
			
			AbsDevice taskDevice = this.deviceList.getOrDefault(deviceUUID, null);
			
			if(taskDevice == null)
			{// 가져오지 못했을경우 추상 장치 객체 생성.
				switch(deviceType)
				{
				case DeviceConsent.TYPE_NAME:
					taskDevice = new DeviceConsent(deviceUUID);
					break;
					
				}
			}
			
		}

		
	}
	
	private void start(NextTask nextTask)
	{
		PLC_IoT_Core.CONS.push("장치 관리자 빌드 시작.");
		this.cycleManager.addReceiveHandler(this);
		
		ResultSet deviceListSql = this.dbConnector.sqlQuery("select * from device");
		
		try
		{
			while(deviceListSql.next())
			{
				System.out.print(deviceListSql.getString(1) + " ");
				System.out.print(deviceListSql.getString(2) + " ");
				System.out.print(deviceListSql.getString(3) + " ");
				System.out.println(deviceListSql.getString(4) + " ");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
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
