package kr.dja.plciot.Task;

public class TaskLock
{
	private final Lockable lockInst;
	private boolean lock;
	
	TaskLock(Lockable lockInst)
	{
		this.lockInst = lockInst;
		this.lock = false;
	}
	
	public synchronized void lock()
	{
		if(!this.lock)
		{
			this.lock = true;
			this.lockInst.noticeChange(this);
		}
	}
	
	public synchronized void unlock()
	{
		if(this.lock)
		{
			this.lock = false;
			this.lockInst.noticeChange(this);
		}
	}
	
	public boolean getState()
	{
		return this.lock;
	}
}
