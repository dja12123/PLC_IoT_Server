package kr.dja.plciot.WebIO;

import kr.dja.plciot.Database.IDatabaseHandler;
import kr.dja.plciot.Device.DeviceManager;
import kr.dja.plciot.Device.IDeviceHandler;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.Data.DeviceInfoChange;
import kr.dja.plciot.WebIO.Data.DevicePowerChange;
import kr.dja.plciot.WebIO.DataFlow.DeviceRealtimeGraph.DeviceRealTimeGraphManager;
import kr.dja.plciot.WebIO.DataFlow.DeviceRealtimePowerChange.RealtimePowerChangeManager;
import kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph.RealTimeGraphManager;

public class WebIOManager implements IMultiThreadTaskCallback
{
	private final IWebSocketReceiveObservable webSocketHandler;
	private IDatabaseHandler dbHandler;
	private final IDeviceHandler deviceHandler;
	
	private final RealTimeGraphManager realtimeGraphManager;
	private final DeviceRealTimeGraphManager deviceRealTimeGraphManager;
	private final RealtimePowerChangeManager realTimePowerManager;
	
	private final DeviceInfoChange deviceInfoChange;
	private final DevicePowerChange devicePowerChange;
	
	public WebIOManager(IWebSocketReceiveObservable webSocketHandler, IDatabaseHandler dbHandler, IDeviceHandler deviceHandler)
	{
		this.webSocketHandler = webSocketHandler;
		this.dbHandler = dbHandler;
		this.deviceHandler = deviceHandler;
		
		this.realtimeGraphManager = new RealTimeGraphManager(this.webSocketHandler, this.deviceHandler);
		this.deviceRealTimeGraphManager = new DeviceRealTimeGraphManager(this.webSocketHandler, this.deviceHandler);
		this.realTimePowerManager = new RealtimePowerChangeManager(this.webSocketHandler, this.deviceHandler);
		
		this.deviceInfoChange = new DeviceInfoChange(this.webSocketHandler, this.dbHandler);
		this.devicePowerChange = new DevicePowerChange(this.webSocketHandler, this.deviceHandler);
	}
	
	private void start(NextTask nextTask)
	{
		nextTask.nextTask();
	}
	
	private void shutdown(NextTask nextTask)
	{
		this.realtimeGraphManager.shutdown();
		this.deviceRealTimeGraphManager.shutdown();
		this.realTimePowerManager.shutdown();
		
		this.deviceInfoChange.shutdown();
		this.devicePowerChange.shutdown();
		
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
