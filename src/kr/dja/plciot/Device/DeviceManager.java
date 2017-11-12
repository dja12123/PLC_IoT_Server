package kr.dja.plciot.Device;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Database.DatabaseConnector;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.AbsDevice.DataFlow.DeviceConsent;
import kr.dja.plciot.Device.AbsDevice.DataFlow.DeviceSwitch;
import kr.dja.plciot.Device.TaskManager.RealTimeDataHandler;
import kr.dja.plciot.LowLevelConnection.ConnectionManager;
import kr.dja.plciot.LowLevelConnection.INewConnectionHandler;
import kr.dja.plciot.LowLevelConnection.PacketProcess;
import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class DeviceManager implements IDeviceList, INewConnectionHandler, IPacketCycleUser, IMultiThreadTaskCallback
{
	public static final int DEFAULT_DEVICE_PORT = 50011;
	public static final String DEVICE_REGISTER = "register";
	public static final String DEVICE_REGISTER_OK = "registerok";
	
	private final ConnectionManager cycleManager;
	private final DatabaseConnector dbConnector;
	private final Map<String, AbsDevice> deviceList;
	
	private final RealTimeDataHandler realTimeDataHandler;
		
	public DeviceManager(ConnectionManager connectionManager, DatabaseConnector dbConnector)
	{
		this.cycleManager = connectionManager;
		this.deviceList = new HashMap<String, AbsDevice>();
		this.dbConnector = dbConnector;
		
		this.realTimeDataHandler = new RealTimeDataHandler();
	}
	
	@Override
	public Iterator<AbsDevice> getIterator()
	{
		return this.deviceList.values().iterator();
	}

	@Override
	public AbsDevice getDeviceFromMac(String mac)
	{
		return this.deviceList.get(mac);
	}
	
	@Override
	public IPacketCycleUser createConnection(String uuid, String name)
	{// 장치 ID 넘어옴
		System.out.println("새 연결 형성됨." + this.deviceList.size());
		AbsDevice receiveTarget = this.deviceList.getOrDefault(uuid, null);
		if(receiveTarget != null)
		{
			return receiveTarget;
		}
		if(name.equals(DEVICE_REGISTER))
		{
			PLC_IoT_Core.CONS.push("장치 등록 패이즈 시작.");
			return this;
		}
		
		return this;
	}
	
	@Override
	public void packetSendCallback(boolean success, String name, String data)
	{
		if(name.equals(DEVICE_REGISTER_OK))
		{
			PLC_IoT_Core.CONS.push("장치 서버 바인딩 성공.");
		}
	}

	@Override
	public void packetReceiveCallback(InetAddress addr, String macAddr, String name, String data)
	{
		if(name.equals(DEVICE_REGISTER))
		{
			this.deviceRegisterTask(addr, macAddr, data);
		}
	}
	
	private void deviceRegisterTask(InetAddress addr, String macAddr, String data)
	{
		PLC_IoT_Core.CONS.push("장치 등록 작업 시작.");
		try
		{
			String[] splitData = data.split(PacketProcess.DEFAULT_SPLIT_REGEX);
			byte[] receiveInetByte = new byte[4];
			String deviceType = splitData[4];
			AbsDevice device = null;
			for(int i = 0; i < 4; ++i)
			{
				receiveInetByte[i] = (byte) Integer.parseInt(splitData[i]);
			}
			InetAddress receiveInet = InetAddress.getByAddress(receiveInetByte);
			
			if(!addr.equals(receiveInet))
			{
				PLC_IoT_Core.CONS.push("주소 불일치 오류.");
				return;
			}
			
			PLC_IoT_Core.CONS.push("장치 등록 요청이 확인됨: mac="+macAddr+" type="+deviceType);
			switch (deviceType)
			{
			case DeviceConsent.TYPE_NAME:
				device = new DeviceConsent(macAddr, this.cycleManager, this.realTimeDataHandler, this.dbConnector);
				break;
			case DeviceSwitch.TYPE_NAME:
				device = new DeviceSwitch(macAddr, this.cycleManager, this.realTimeDataHandler, this.dbConnector);
				break;
			}
			
			if(device == null)
			{
				PLC_IoT_Core.CONS.push("정의되지 않은 장치 오류.");
				return;
			}
			
			this.deviceList.put(macAddr, device);
			
			ResultSet rs = this.dbConnector.sqlQuery("select * from device where mac_id = '"+macAddr+"';");
			if(rs.next())
			{
				PLC_IoT_Core.CONS.push("등록된 장치 바인딩: " + rs.getString(1));
			}
			else
			{
				PLC_IoT_Core.CONS.push("미등록 장치 바인딩.");
				this.dbConnector.sqlUpdate("insert into waiting_device VALUES('"+macAddr+"','"+deviceType+"');");
			}
			
			this.cycleManager.startSendCycle(addr, DEFAULT_DEVICE_PORT, macAddr, DEVICE_REGISTER_OK, "", this);
			// 등록 완료 확인 메세지 전송.
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
