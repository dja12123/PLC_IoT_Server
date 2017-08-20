package kr.dja.plciot.UI;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

import kr.dja.plciot.Log.Console;
import kr.dja.plciot.Task.MultiThread.MultiThreadTaskOperator;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class MainFrame extends JFrame implements IMultiThreadTaskCallback
{
	private static final long serialVersionUID = 1L;
	
	private final ConsoleUI consoleUI;
	
	public MainFrame(Console console)
	{
		
		this.consoleUI = new ConsoleUI(console);
		Container contantPane = this.getContentPane();
		
		this.setTitle("PLC IoT Server");
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		
		contantPane.setLayout(new BorderLayout());
		contantPane.add(this.consoleUI, BorderLayout.CENTER);
		
		this.setVisible(true);
	}

	@Override
	public void executeTask(TaskOption option, NextTask next)
	{
		if(option == TaskOption.SHUTDOWN)
		{
			this.consoleUI.shutdown();
			next.nextTask();
		}
	}
	
}
