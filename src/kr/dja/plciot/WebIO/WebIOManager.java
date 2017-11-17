package kr.dja.plciot.WebIO;

import kr.dja.plciot.Device.DeviceManager;
import kr.dja.plciot.Device.IDeviceView;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph.RealTimeGraphManager;

public class WebIOManager implements IMultiThreadTaskCallback
{
	private final IWebSocketReceiveObservable observable;
	
	
	private final RealTimeGraphManager realTimeGraphManager;
	private final IDeviceView deviceList;
	
	private final DeviceInfoChange deviceInfoChange;
	private final DevicePowerChange devicePowerChange;
	
	public WebIOManager(IWebSocketReceiveObservable observable, IDeviceView deviceList)
	{
		this.observable = observable;
		this.deviceList = deviceList;
		
		this.realTimeGraphManager = new RealTimeGraphManager(deviceList);
		this.deviceInfoChange = new DeviceInfoChange();
		this.devicePowerChange = new DevicePowerChange(deviceList);
	}
	
	private void start(NextTask nextTask)
	{
		this.observable.addObserver(RealTimeGraphManager.GRAPH_REQ, this.realTimeGraphManager);
		this.observable.addObserver(DeviceInfoChange.DEVICE_INFO_CHANGE_REQ, this.deviceInfoChange);
		this.observable.addObserver(DevicePowerChange.DEVICE_POWER_CHANGE_REQ, this.devicePowerChange);
		nextTask.nextTask();
	}
	
	private void shutdown(NextTask nextTask)
	{
		this.observable.deleteObserver(RealTimeGraphManager.GRAPH_REQ);
		this.realTimeGraphManager.shutdown();
		this.observable.deleteObserver(DeviceInfoChange.DEVICE_INFO_CHANGE_REQ);
		this.observable.deleteObserver(DevicePowerChange.DEVICE_POWER_CHANGE_REQ);
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
