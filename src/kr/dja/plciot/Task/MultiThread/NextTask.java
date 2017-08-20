package kr.dja.plciot.Task.MultiThread;

public class NextTask
{// nextTask를 캡슐화 하기 위한 클래스.
	
	private final MultiThreadTaskOperator oper;
	
	NextTask(MultiThreadTaskOperator oper)
	{
		this.oper = oper;
	}
	
	public void nextTask()
	{
		this.oper.nextTask();
	}
	
	public void error(Exception e, String message)
	{
		this.oper.error(e, message);
	}
}
