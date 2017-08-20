package kr.dja.plciot.Task.MultiThread;

public interface IMultiThreadTaskCallback
{
	void executeTask(TaskOption option, NextTask nextTask);
}
