package kr.dja.plciot.Task.Serial;

import java.util.concurrent.ConcurrentLinkedQueue;

import kr.dja.plciot.Task.Lockable;

public class SerializeTaskOperator extends Lockable implements Runnable
{
	private ConcurrentLinkedQueue<ISerialTaskCallback> taskQueue;
	private boolean threadSwitch;
	private int taskInterval;
	private Thread taskThread;
	
	public SerializeTaskOperator()
	{
		this(0);
	}
	
	public SerializeTaskOperator(int taskInterval)
	{
		this.taskQueue = new ConcurrentLinkedQueue<ISerialTaskCallback>();
		this.threadSwitch = true;
		this.taskInterval = taskInterval;
		
		this.taskThread = new Thread(this);
		this.taskThread.run();
	}
	
	public void addTask(ISerialTaskCallback callback)
	{// 작업 추가.
		this.taskQueue.add(callback);
	}
	
	public void threadOff()
	{// 쓰레드 종료.
		this.threadSwitch = false;
	}
	
	@Override
	public void run()
	{
		while(this.threadSwitch)
		{
			long taskStartTime = System.currentTimeMillis();
			System.out.println("TASKSIZE: " + this.taskQueue.size());
			
			if(!this.isLock() && !this.taskQueue.isEmpty())
			{// 잠기지 않았고, 테스크 큐 있을떄.
				this.taskQueue.poll().executeTask();
			}
			
			long taskTime = System.currentTimeMillis() - taskStartTime;
			
			try
			{
				if(taskTime >= this.taskInterval)
				{
					// 작업 시간이 너무 오래 걸렸을때.
					// taskTime을 최대치로 조정해서 쓰레드가 0ms만큼 대기하도록 한다.
					taskTime = this.taskInterval;
				}
				
				Thread.sleep(this.taskInterval - taskTime);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
