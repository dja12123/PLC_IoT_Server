package kr.dja.plciot.Task.MultiThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kr.dja.plciot.Task.Lockable;

public class MultiThreadTaskOperator extends Lockable implements Runnable
{
	private final TaskOption option;
	private List<IMultiThreadTaskCallback> taskQueue;
	private Thread nowTaskThread;
	private boolean taskLockFlag;
	private boolean startFlag;
	
	public MultiThreadTaskOperator(TaskOption option)
	{
		this.option = option;
		this.taskQueue = Collections.synchronizedList(new ArrayList<IMultiThreadTaskCallback>());
	}
	
	public MultiThreadTaskOperator(TaskOption option, IMultiThreadTaskCallback[] callbackArr)
	{
		this(option);
		
		for(IMultiThreadTaskCallback task : callbackArr)
		{
			this.taskQueue.add(task);
		}
	}
	
	public void addTask(IMultiThreadTaskCallback callback)
	{
		this.taskQueue.add(callback);
	}
	
	public synchronized void insertTask(IMultiThreadTaskCallback callback)
	{// 현재 작업중인 위치 바로 뒤에 작업을 삽입합니다.
		this.taskQueue.add(0, callback);
	}
	
	public synchronized void start()
	{
		if(!this.startFlag)
		{
			this.nextTask();
			this.startFlag = true;
		}
		else
		{
			new Exception("MultiThreadTask Already Start Exception").printStackTrace();
		}
	}
	
	synchronized void nextTask()
	{// 실행이 끝났을때 이 메소드 호출.
		if(this.nowTaskThread != null)
		{
			new Exception("MultiThreadTask TaskException").printStackTrace();
		}
		Thread t = new Thread(this);
		t.start();
		this.nowTaskThread = t;
	}
	
	void error(Exception e, String message)
	{
		System.out.println("순차 작업중 오류 발생");
		System.out.println(message);
		e.printStackTrace();
	}
	
	@Override
	public synchronized void unLock()
	{
		if(this.taskLockFlag)
		{
			this.nowTaskThread.interrupt();
		}
	}

	@Override
	public void run()
	{
		IMultiThreadTaskCallback task;
		synchronized(this)
		{
			if(this.isLock())
			{
				this.taskLockFlag = true;
				try
				{
					while(true)
					{
						Thread.sleep(1000);
					}
				}
				catch(InterruptedException e)
				{
					System.out.println("unlock");
				}
				this.taskLockFlag = false;
			}
			
			if(!this.taskQueue.isEmpty())
			{
				task = this.taskQueue.get(0);
				this.taskQueue.remove(0);
				System.out.println("COUNT: " + this.taskQueue.size());
				System.out.println(task != null);
			}
			else
			{
				return;
			}
			
			this.nowTaskThread = null;
		}
		 
		task.executeTask(this.option, new NextTask(this));
	}
}
