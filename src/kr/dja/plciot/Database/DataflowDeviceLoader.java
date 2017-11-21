package kr.dja.plciot.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.IDeviceEventObserver;
import kr.dja.plciot.Device.IDeviceHandler;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.AbsDevice.DataFlow.AbsDataFlowDevice;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class DataflowDeviceLoader implements IMultiThreadTaskCallback, Runnable, IDeviceEventObserver
{
	private final int DB_STORE_INTEVAL = 5000;
	
	private final IDatabaseHandler dbHandler;
	private final IDeviceHandler deviceHandler;
	
	private boolean runFlag;
	private Thread thread;

	private NextTask nextTask;
	
	private Map<String, List<String>> deviceValueTypeMap;
	
	private Map<String, DeviceValue> recodeMap;
	
	public DataflowDeviceLoader(IDatabaseHandler dbHandler, IDeviceHandler deviceHandler)
	{
		this.dbHandler = dbHandler;
		this.deviceHandler = deviceHandler;
		
		this.thread = new Thread(this);
		
		this.deviceValueTypeMap = new HashMap<String, List<String>>();
		this.recodeMap = new HashMap<String, DeviceValue>();
	}
	
	@Override
	public void run()
	{
		PLC_IoT_Core.CONS.push("실시간 데이터 푸시 관리자 활성화.");
		this.nextTask.nextTask();// 시작시 흐름 넘김.
		while(this.runFlag)
		{
			synchronized (this)
			{
				for(String macAddr : this.recodeMap.keySet())
				{
					DeviceValue recode = this.recodeMap.get(macAddr);
					int[] valueArr = recode.getValues();
					int valueCount = recode.getCount();
					int recodeInfoID = -1;
					try
					{
						ResultSet rs = this.dbHandler.sqlQuery("select max(idpk) from recode_info;");
						rs.next();
						recodeInfoID = rs.getInt(1);
						++recodeInfoID;
						this.dbHandler.sqlUpdate("insert into recode_info values('"+macAddr+"', sysdate(), "+recodeInfoID+");");
					}
					catch (SQLException e)
					{
						e.printStackTrace();
						continue;
					}
					
					for(int i = 0; i < valueArr.length; ++i)
					{
						int dbStoreValue = valueArr[i] / valueCount;
						String dbStoreType = this.deviceValueTypeMap.get(recode.deviceType).get(i);
						this.dbHandler.sqlUpdate("insert into recode_data values("+recodeInfoID+
								", "+dbStoreType+", "+dbStoreValue+");");
					}
				}
				this.recodeMap.clear();
			}
			
			//PLC_IoT_Core.CONS.push("기록 완료.");
			try
			{
				Thread.sleep(DB_STORE_INTEVAL);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		PLC_IoT_Core.CONS.push("실시간 데이터 푸시 관리자 비활성화.");
		this.nextTask.nextTask();// 끝날시 흐름 넘김.
	}
	
	@Override
	public void deviceEvent(AbsDevice device, String key, String data)
	{
		System.out.println(key + " " + data);
		if(key != AbsDataFlowDevice.SENSOR_DATA_EVENT) return;
		if(!(device instanceof AbsDataFlowDevice)) return;
		
		AbsDataFlowDevice dataflowDevice = (AbsDataFlowDevice)device;
		
		List<String> deviceDataTypeList = this.deviceValueTypeMap.get(dataflowDevice.getDeviceType());
		
		DeviceValue resultSet = this.recodeMap.getOrDefault(dataflowDevice.macAddr, null);
		if(resultSet == null)
		{
			resultSet = new DeviceValue(deviceDataTypeList.size(), dataflowDevice.getDeviceType());
			this.recodeMap.put(device.macAddr, resultSet);
		}
		
		for(int i = 0; i < deviceDataTypeList.size(); ++i)
		{
			int deviceValue = dataflowDevice.getDeviceValue(deviceDataTypeList.get(i));
			resultSet.storeValue(deviceValue);
		}
	}
	
	private void start(NextTask nextTask)
	{
		PLC_IoT_Core.CONS.push("실시간 데이터 푸시 관리자 시작.");
		ResultSet rs = this.dbHandler.sqlQuery("select * from value_by_device_type;");
		try
		{
			while(rs.next())
			{
				String deviceType = rs.getString(1);
				String deviceValue = rs.getString(2);
				List<String> deviceValues = this.deviceValueTypeMap.getOrDefault(deviceType, null);
				if(deviceValues == null)
				{
					deviceValues = new ArrayList<String>();
					this.deviceValueTypeMap.put(deviceType, deviceValues);
				}
				deviceValues.add(deviceValue);
				PLC_IoT_Core.CONS.push("장치 " + deviceType + "에 대한 데이터 타입 " + deviceValue + " 로드.");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		this.deviceHandler.addObserver(AbsDataFlowDevice.SENSOR_DATA_EVENT, this);
		
		this.runFlag = true;
		this.nextTask = nextTask;
		this.thread.start();
	}
	
	private void shutdown(NextTask nextTask)
	{
		PLC_IoT_Core.CONS.push("실시간 데이터 푸시 관리자 종료 시작.");
		this.deviceHandler.deleteObserver(this);
		this.nextTask = nextTask;
		this.runFlag = false;
		this.thread.interrupt();
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
class DeviceValue
{
	private final int dataCount;
	public final String deviceType;
	
	private int count;
	private int[] values;
	
	private int putCount;
	
	public DeviceValue(int dataCount, String deviceType)
	{
		this.dataCount = dataCount;
		this.deviceType = deviceType;
		this.count = 0;
		this.values = new int[this.dataCount];
		this.putCount = 0;
	}
	
	public void storeValue(int data)
	{
		this.values[this.putCount] += data;
		if(++this.putCount >= this.dataCount)
		{
			this.putCount = 0;
			for(int i = 0; i < this.dataCount; ++i) this.values[i] = 0;
			++this.count;
		}
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public int[] getValues()
	{
		return this.values;
	}
	
}