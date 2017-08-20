package kr.dja.plciot.Task.MultiThread;

public interface IMTTaskCallback
{
	void executeTask(TaskOption option, MTTaskOperator operator);
}
