package kr.dja.plciot.WebIO;

import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.TaskOption;
import kr.dja.plciot.WebConnector.IWebSocketReceiveObservable;
import kr.dja.plciot.WebIO.DataFlow.MainRealTimeGraph.RealTimeGraphManager;

public class WebIOManager implements IMultiThreadTaskCallback
{
	private final IWebSocketReceiveObservable observable;
	
	private final RealTimeGraphManager realTimeGraphManager;
	
	private static final String GRAPH_REQ = "GETGRAPH";
	
	public WebIOManager(IWebSocketReceiveObservable observable)
	{
		this.observable = observable;
		
		this.realTimeGraphManager = new RealTimeGraphManager();
	}
	
	private void start(NextTask nextTask)
	{
		this.observable.addObserver(GRAPH_REQ, this.realTimeGraphManager);
		nextTask.nextTask();
	}
	
	private void shutdown(NextTask nextTask)
	{
		this.observable.deleteObserver(GRAPH_REQ);
		this.realTimeGraphManager.shutdown();
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
