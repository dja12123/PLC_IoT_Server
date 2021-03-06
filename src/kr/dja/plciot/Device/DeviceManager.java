package kr.dja.plciot.Device;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Database.DatabaseConnector;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.AbsDevice.DataFlow.DeviceConsent;
import kr.dja.plciot.Device.AbsDevice.DataFlow.DeviceSwitch;
import kr.dja.plciot.LowLevelConnection.ConnectionManager;
import kr.dja.plciot.LowLevelConnection.INewConnectionHandler;
import kr.dja.plciot.LowLevelConnection.PacketProcess;
import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;
import kr.dja.plciot.MultiValueMap;

public class DeviceManager implements IDeviceHandler, INewConnectionHandler, IPacketCycleUser, IMultiThreadTaskCallback, IDeviceEventObserver
{
	public static final int DEFAULT_DEVICE_PORT = 50011;
	public static final String DEVICE_REGISTER = "register";
	public static final String DEVICE_REGISTER_OK = "registerok";
	
	private final ConnectionManager cycleManager;
	private final DatabaseConnector dbConnector;
	private final Map<String, AbsDevice> deviceList;
	
	private final MultiValueMap<String, IDeviceEventObserver> deviceEventListenerList;
	
	public DeviceManager(ConnectionManager connectionManager, DatabaseConnector dbConnector)
	{
		this.cycleManager = connectionManager;
		this.deviceList = new HashMap<String, AbsDevice>();
		this.dbConnector = dbConnector;
		
		this.deviceEventListenerList = new MultiValueMap<String, IDeviceEventObserver>();
	}

	@Override
	public void addObserver(String key, IDeviceEventObserver observer)
	{
		this.deviceEventListenerList.put(key, observer);
		PLC_IoT_Core.CONS.push("장치 데이터 이벤트 바인딩 key: " + key);
	}

	@Override
	public void deleteObserver(IDeviceEventObserver observer)
	{
		this.deviceEventListenerList.removeValue(observer);
	}
	
	@Override
	public void deviceEvent(AbsDevice device, String key, String data)
	{
		List<IDeviceEventObserver> observerList = this.deviceEventListenerList.get(key);
		System.out.println(observerList);
		if(observerList == null) return;
		for(IDeviceEventObserver observer : observerList)
		{
			observer.deviceEvent(device, key, data);
		}
	}

	@Override
	public void deleteObserver(IDeviceEventObserver observer, String key)
	{
		this.deviceEventListenerList.remove(key, observer);
		PLC_IoT_Core.CONS.push("장치 데이터 이벤트 바인딩 해제 key: " + key);
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
				device = new DeviceConsent(macAddr, this.cycleManager, this, this.dbConnector);
				break;
			case DeviceSwitch.TYPE_NAME:
				device = new DeviceSwitch(macAddr, this.cycleManager, this, this.dbConnector);
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
				this.cycleManager.startSendCycle(addr, DEFAULT_DEVICE_PORT, macAddr, DEVICE_REGISTER_OK, "", this);
				return;
			}
			
			PLC_IoT_Core.CONS.push("등록 대기 장치 접속.");
			rs = this.dbConnector.sqlQuery("select macAddr from waiting_device where macAddr = '"+macAddr+"';");
			if(!rs.next())
			{
				this.dbConnector.sqlUpdate("insert into waiting_device VALUES('"+macAddr+"','"+deviceType+"');");
			}
			
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
				String deviceDB = "장치 ";
				deviceDB += deviceListSql.getString(1) + " ";
				deviceDB += deviceListSql.getString(2) + " ";
				deviceDB += deviceListSql.getString(3) + " ";
				deviceDB += deviceListSql.getString(4) + " ";
				deviceDB += "로드.";
				PLC_IoT_Core.CONS.push(deviceDB);
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