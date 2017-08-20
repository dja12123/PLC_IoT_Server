package kr.dja.plciot.Task.MultiThread;

import java.util.concurrent.ConcurrentLinkedQueue;

import kr.dja.plciot.Task.Lockable;

public class MultiThreadTaskOperator extends Lockable implements Runnable
{
	private final TaskOption option;
	private ConcurrentLinkedQueue<IMultiThreadTaskCallback> taskQueue;
	private Thread nowTaskThread;
	private boolean taskLockFlag;
	private boolean startFlag;
	
	public MultiThreadTaskOperator(TaskOption option)
	{
		this.option = option;
		this.taskQueue = new ConcurrentLinkedQueue<IMultiThreadTaskCallback>();
	}
	
	public MultiThreadTaskOperator(TaskOption option, IMultiThreadTaskCallback[] callbackArr)
	{
		this(option);
		
		for(IMultiThreadTaskCallback task : callbackArr)
		{
			this.taskQueue.add(task);
		}
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
				task = this.taskQueue.poll();
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
