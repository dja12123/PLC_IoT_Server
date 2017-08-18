package kr.dja.plciot.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class TaskThread extends Thread
{
	private List<ITaskCallback> taskList;
	private boolean threadSwitch;
	private int taskInterval;
	
	public TaskThread()
	{
		this(0);
	}
	
	public TaskThread(int taskInterval)
	{
		this.taskList = Collections.synchronizedList(new ArrayList<ITaskCallback>());
		this.threadSwitch = true;
		this.taskInterval = taskInterval;
	}
	
	public void addTask(ITaskCallback callback)
	{// 작업 추가.
		this.taskList.add(callback);
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
			Object[] taskArr;
			
			try
			{// 설정한 시간만큼 대기.
				Thread.sleep(this.taskInterval);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			if(this.taskList.size() == 0) continue;
			
			System.out.println("TASKSIZE: " + this.taskList.size());
			synchronized(this)
			{
				taskArr = this.taskList.toArray();
				this.taskList.clear();
			}
			
			for(Object callback : taskArr)
			{// 작업 대기 리스트에 있는 작업 모두 실행.
				long taskStartTime = System.currentTimeMillis();
				
				((ITaskCallback)callback).task();
				
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
		}
	}
}
