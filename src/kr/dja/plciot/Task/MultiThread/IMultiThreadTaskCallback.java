package kr.dja.plciot.Task.MultiThread;

public interface IMultiThreadTaskCallback
{
	void executeTask(TaskOption option, MultiThreadTaskOperator operator);
}
