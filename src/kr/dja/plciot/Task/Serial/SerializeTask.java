package kr.dja.plciot.Task.Serial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

public class SerializeTask extends Thread
{
	private ConcurrentLinkedQueue<ISerialTaskCallback> taskQueue;
	private boolean threadSwitch;
	private int taskInterval;
	
	public SerializeTask()
	{
		this(0);
	}
	
	public SerializeTask(int taskInterval)
	{
		this.taskQueue = new ConcurrentLinkedQueue<ISerialTaskCallback>();
		this.threadSwitch = true;
		this.taskInterval = taskInterval;
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
		boolean sleepFlag;
		while(this.threadSwitch)
		{
			sleepFlag = true;
			System.out.println("TASKSIZE: " + this.taskQueue.size());
			
			while(!this.taskQueue.isEmpty())
			{// 작업 대기 리스트에 있는 작업 모두 실행.
				sleepFlag = false;
				long taskStartTime = System.currentTimeMillis();
				
				this.taskQueue.poll().task();
				
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
				System.out.println(taskTime + " sleep: " + (this.taskInterval - taskTime));
			}
			
			if(sleepFlag)
			{
				try
				{// 설정한 시간만큼 대기.
					Thread.sleep(this.taskInterval);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
