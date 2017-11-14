package kr.dja.plciot.WebIO;

import kr.dja.plciot.Device.DeviceManager;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph.RealTimeGraphManager;

public class WebIOManager implements IMultiThreadTaskCallback
{
	private final IWebSocketReceiveObservable observable;
	
	
	private final RealTimeGraphManager realTimeGraphManager;
	private final DeviceManager deviceManager;
	
	private final DeviceInfoChange deviceInfoChange;
	
	public WebIOManager(IWebSocketReceiveObservable observable, DeviceManager deviceManager)
	{
		this.observable = observable;
		this.deviceManager = deviceManager;
		
		this.realTimeGraphManager = new RealTimeGraphManager(deviceManager);
		this.deviceInfoChange = new DeviceInfoChange();
	}
	
	private void start(NextTask nextTask)
	{
		this.observable.addObserver(RealTimeGraphManager.GRAPH_REQ, this.realTimeGraphManager);
		this.observable.addObserver(DeviceInfoChange.DATA_REQ, this.deviceInfoChange);
		nextTask.nextTask();
	}
	
	private void shutdown(NextTask nextTask)
	{
		this.observable.deleteObserver(RealTimeGraphManager.GRAPH_REQ);
		this.realTimeGraphManager.shutdown();
		this.observable.deleteObserver(DeviceInfoChange.DATA_REQ);
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
