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
			this.lockInst.noticeChange(true, this);
			this.lock = true;
		}
	}
	
	public synchronized void unlock()
	{
		if(this.lock)
		{
			this.lockInst.noticeChange(false, this);
			this.lock = false;
		}
	}
	
	public boolean getState()
	{
		return this.lock;
	}
}
