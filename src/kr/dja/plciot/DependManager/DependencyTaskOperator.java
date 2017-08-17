package kr.dja.plciot.DependManager;

public class DependencyTaskOperator
{
	private int taskIndex;
	
	private final TaskOption option;
	private final IDependencyTask[] callbackArr;
	
	public DependencyTaskOperator(TaskOption option, IDependencyTask[] callbackArr)
	{
		this.option = option;
		this.callbackArr = callbackArr;
		
		this.taskIndex = 0;
	}
	
	public void nextTask()
	{// 실행이 끝났을때 이 메소드 호출.
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.out.println(taskIndex+"실행:"+this.callbackArr[this.taskIndex].getClass());
		new Thread(()->
		{
			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.callbackArr[this.taskIndex++].executeTask(this.option, this);
		}).start();
		System.out.println("실행완료");
	}
	
	public void error(Exception e, String message)
	{
		System.out.println("순차 작업중 오류 발생: " + this.taskIndex + "번 에서");
		System.out.println(message);
		e.printStackTrace();
	}
}
