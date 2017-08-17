package kr.dja.plciot.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import kr.dja.plciot.DependManager.DependencyTaskOperator;
import kr.dja.plciot.DependManager.IDependencyTask;
import kr.dja.plciot.DependManager.TaskOption;

public class Console extends Observable implements IDependencyTask
{// 콘솔 로그 관리 클래스.
	private LogSaver logSaver;
	
	private int numOfLog;
	private List<ConsoleMessage> consoleList;
	
	public Console()
	{
		this.logSaver = new LogSaver(this);
		this.numOfLog = 0;
		this.consoleList = Collections.synchronizedList(new ArrayList<ConsoleMessage>());
	}
	
	public void push(String message)
	{
		this.push(message, null);
	}
	
	public void push(String message, Object sender)
	{
		ConsoleMessage messageObj = new ConsoleMessage(sender, message, this.numOfLog++);
		this.consoleList.add(messageObj);
		
		System.out.println(messageObj.getMessage());
		
		this.setChanged();
		this.notifyObservers(messageObj);
	}

	@Override
	public void executeTask(TaskOption option, DependencyTaskOperator operator)
	{
		if(option == TaskOption.SHUTDOWN)
		{//TODO 로그 저장 처리
			operator.nextTask();
		}
	}

}