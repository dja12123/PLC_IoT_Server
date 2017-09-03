package kr.dja.plciot;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import kr.dja.plciot.Database.DatabaseConnector;
import kr.dja.plciot.Device.DeviceManager;
import kr.dja.plciot.DeviceConnection.ConnectionManager;
import kr.dja.plciot.DeviceConnection.ConnectionManager.ConnectionManagerBuilder;
import kr.dja.plciot.Log.Console;
import kr.dja.plciot.Task.MultiThread.MultiThreadTaskOperator;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.TaskLock;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.TaskOption;
import kr.dja.plciot.UI.MainFrame;
import kr.dja.plciot.Web.WebServer;

public class PLC_IoT_Core implements IMultiThreadTaskCallback
{
	private static PLC_IoT_Core MainInstance;
	public static final Console CONS = new Console();
	
	private final MainFrame mainFrame;
	private final DatabaseConnector dbManager;
	private final ConnectionManager deviceConnectionManager;
	private final DeviceManager deviceManager;
	private final WebServer webServer;
	
	private PLC_IoT_Core()
	{//TEST
		this.mainFrame = new MainFrame();
		this.dbManager = new DatabaseConnector();
		this.deviceConnectionManager = new ConnectionManager();
		this.deviceManager = new DeviceManager(this.deviceConnectionManager);
		this.webServer = new WebServer();
		
		ConnectionManagerBuilder connectionManagerBuilder = new ConnectionManagerBuilder(this.deviceConnectionManager);
		connectionManagerBuilder.setReceiveRegister(this.deviceManager);
		
		IMultiThreadTaskCallback[] startTaskArr = new IMultiThreadTaskCallback[]
				{this.dbManager, connectionManagerBuilder, this.webServer, this};
		MultiThreadTaskOperator serverStartOperator = new MultiThreadTaskOperator(TaskOption.START, startTaskArr);
		
		IMultiThreadTaskCallback[] shutdownTaskArr = new IMultiThreadTaskCallback[]
				{connectionManagerBuilder, this.webServer, this.dbManager, CONS, this.mainFrame, this};
		MultiThreadTaskOperator serverShutdownOperator = new MultiThreadTaskOperator(TaskOption.SHUTDOWN, shutdownTaskArr);
		
		serverStartOperator.start();
		
		this.mainFrame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				CONS.push("서버 종료 시작.");
				serverShutdownOperator.start();
			}
		});
	}
	
	public static void main(String[] args)
	{
		MainInstance = new PLC_IoT_Core();
	}

	@Override
	public void executeTask(TaskOption option, NextTask next)
	{
		if(option == TaskOption.START)
		{
			CONS.push("서버 시작 완료.");
		}
		if(option == TaskOption.SHUTDOWN)
		{
			CONS.push("서버 종료 완료.");
			System.exit(0);
		}
	}
}