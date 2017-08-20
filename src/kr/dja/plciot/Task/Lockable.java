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
	
	protected void unLock(){}
	
	protected void lock(){}
	
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
			lockInst.unlock();
			this.lockList.remove(lockInst);
			System.out.println(this.lockList.size());
		}
	}
	
	synchronized void noticeChange(TaskLock lockInst)
	{
		// state = true Lock
		// state = false UnLock
		if(this.lockList.contains(lockInst))
		{
			boolean beforeLock = this.isLock();
			if(lockInst.getState())
			{
				++this.lockCount;
			}
			else
			{
				--this.lockCount;
			}
			
			boolean afterLock = this.isLock();
			if(afterLock != beforeLock)
			{
				if(afterLock)
				{
					this.lock();
				}
				else
				{
					this.unLock();
				}
				System.out.println(this.lockCount + " " + afterLock + " " + this.lockList.size());
			}
		}
	}
}