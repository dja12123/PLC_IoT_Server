package kr.dja.plciot.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Lockable
{
	private List<TaskLock> lockList;
	private int lockCount;
	
	public Lockable()
	{
		this.lockList = Collections.synchronizedList(new ArrayList<TaskLock>());
		this.lockCount = 0;
	}
	
	public boolean isLock()
	{
		if(this.lockCount > 0) return true;
		return false;
	}
	
	public synchronized TaskLock createLock()
	{
		TaskLock lock = new TaskLock(this);
		this.lockList.add(lock);
		return lock;
	}
	
	public synchronized void removeLock(TaskLock lockInst)
	{
		if(this.lockList.contains(lockInst))
		{
			if(lockInst.getState())
			{// 락이 걸린 상태라면.
				--this.lockCount;
			}
			this.lockList.remove(lockInst);
		}
		
	}
	
	synchronized void noticeChange(boolean state, TaskLock lockInst)
	{
		// state = true Lock
		// state = false UnLock
		if(this.lockList.contains(lockInst))
		{
			if(state)
			{
				++this.lockCount;
			}
			else
			{
				--this.lockCount;
			}
			System.out.println(this.lockList.size() + " lockCount " + lockCount);
		}
	}
}