package kr.dja.plciot;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import kr.dja.plciot.Database.DatabaseConnector;
import kr.dja.plciot.DependManager.DependencyTaskOperator;
import kr.dja.plciot.DependManager.IDependencyTask;
import kr.dja.plciot.DependManager.TaskOption;
import kr.dja.plciot.Device.DeviceConnection;
import kr.dja.plciot.Log.Console;
import kr.dja.plciot.UI.MainFrame;
import kr.dja.plciot.Web.WebServer;

public class PLC_IoT_Core implements IDependencyTask
{
	private static PLC_IoT_Core MainInstance;
	
	public final MainFrame mainFrame;
	public final Console console;
	public final DatabaseConnector dbManager;
	public final WebServer webServer;
	
	private PLC_IoT_Core()
	{//TESTa
		this.console = new Console();
		this.mainFrame = new MainFrame(this.console);
		this.dbManager = new DatabaseConnector(this.console);
		this.webServer = new WebServer(this.console);
		
		IDependencyTask[] startTaskArr = new IDependencyTask[]{this.dbManager, this.webServer, this};
		DependencyTaskOperator serverStartOperator = new DependencyTaskOperator(TaskOption.START, startTaskArr);
		
		IDependencyTask[] shutdownTaskArr = new IDependencyTask[]{this.webServer, this.dbManager, this.console, this.mainFrame, this};
		DependencyTaskOperator serverShutdownOperator = new DependencyTaskOperator(TaskOption.SHUTDOWN, shutdownTaskArr);
		
		serverStartOperator.nextTask();
		
		this.mainFrame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				serverShutdownOperator.nextTask();
			}
		});
	}
	
	public static void main(String[] args)
	{
		MainInstance = new PLC_IoT_Core();
		
	}

	@Override
	public void executeTask(TaskOption option, DependencyTaskOperator operator)
	{
		if(option == TaskOption.START)
		{
			this.console.push("서버 시작 완료");
		}
		if(option == TaskOption.SHUTDOWN)
		{
			System.out.println("서버 종료 완료");
			System.exit(0);
		}
	}
}
