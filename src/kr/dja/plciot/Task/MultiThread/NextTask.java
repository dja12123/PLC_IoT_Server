package kr.dja.plciot.Task.MultiThread;

import kr.dja.plciot.Task.Lockable;
import kr.dja.plciot.Task.TaskLock;

public class NextTask extends Lockable
{// 순차 작업에서 해당 작업 수행자에게 작업을 컨트롤 할 수 있는 인터페이스 제공.
	
	private final MultiThreadTaskOperator oper;
	private boolean executeTaskFlag;
	private boolean lockTaskFlag;
	
	NextTask(MultiThreadTaskOperator oper)
	{
		this.oper = oper;
		this.executeTaskFlag = false;
		this.lockTaskFlag = false;
	}
	
	public void insertTask(IMultiThreadTaskCallback callback)
	{
		this.oper.insertTask(callback);
	}
	
	public void nextTask()
	{// 한 작업 단위에서 작업 실행이 직렬화 되어 있을경우 사용합니다.
	 // 이 메소드를 호출할 경우 다음 작업을 ready 상태로 바꿉니다.
		
		if(this.executeTaskFlag)
		{// 작업이 이미 다음 작업으로 넘어갔을때.
			
			new Exception("Task Already Executed").printStackTrace();
			return;
		}
		if(this.lockTaskFlag)
		{// 다음 작업 실행이 이 클래스에 위임 되었을때.
			
			new Exception("Task Is Lock Task").printStackTrace();
			return;
		}
		this.privateNextTask();
	}
	
	@Override
	public TaskLock createLock()
	{// 이 클래스에 다음 작업 실행을 위임하는 메소드 입니다.
	 // 락이 풀리면 자동으로 다음 작업을 실행하며, nextTask로 다음 작업을 수행할 수 없습니다.
	 // 한 작업 단위에서 여러 쓰레드의 작업이 모두 끝난다음 다음 작업으로 넘어가야 할때 사용합니다.
		
		this.lockTaskFlag = true;
		TaskLock lock = super.createLock();
		lock.lock();
		return lock;
	}
	
	private void privateNextTask()
	{
		this.oper.nextTask();
		this.executeTaskFlag = true;
	}
	
	@Override
	protected void unLock()
	{// 다음 작업을 수행합니다.
		this.privateNextTask();
	}
	
	public void error(Exception e, String message)
	{
		this.oper.error(e, message);
	}
}
