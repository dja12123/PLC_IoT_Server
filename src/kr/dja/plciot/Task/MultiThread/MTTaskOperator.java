package kr.dja.plciot.Task.MultiThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import kr.dja.plciot.Task.Serial.ISerialTaskCallback;

public class MTTaskOperator
{
	private final TaskOption option;
	private ConcurrentLinkedQueue<IMTTaskCallback> taskQueue;
	private List<TaskLock> lock;
	
	public MTTaskOperator(TaskOption option)
	{
		this.option = option;
		this.taskQueue = new ConcurrentLinkedQueue<IMTTaskCallback>();
		this.lock = Collections.synchronizedList(new ArrayList<TaskLock>());
	}
	
	public MTTaskOperator(TaskOption option, IMTTaskCallback[] callbackArr)
	{
		this(option);
		
		for(IMTTaskCallback task : callbackArr)
		{
			this.taskQueue.add(task);
		}
	}
	
	public void nextTask()
	{// 실행이 끝났을때 이 메소드 호출.
		new Thread(()->
		{
			if(!this.taskQueue.isEmpty())
			{
				this.taskQueue.poll().executeTask(this.option, this);
			}
		}).start();
	}
	
	public void error(Exception e, String message)
	{
		System.out.println("순차 작업중 오류 발생");
		System.out.println(message);
		e.printStackTrace();
	}
	
	public TaskLock createLock()
	{
		TaskLock lock = new TaskLock();
		lock.unlock();
		this.lock.add(lock);
		return lock;
	}
}
