package kr.dja.plciot.DependManager;

public interface IDependencyTask
{
	void executeTask(TaskOption option, DependencyTaskOperator operator);
}
