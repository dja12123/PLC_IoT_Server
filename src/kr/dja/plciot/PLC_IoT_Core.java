package kr.dja.plciot;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import kr.dja.plciot.Database.DatabaseConnector;
import kr.dja.plciot.Database.DataflowDeviceLoader;
import kr.dja.plciot.Device.DeviceManager;
import kr.dja.plciot.Log.Console;
import kr.dja.plciot.LowLevelConnection.ConnectionManager;
import kr.dja.plciot.LowLevelConnection.ConnectionManager.ConnectionManagerBuilder;
import kr.dja.plciot.LowLevelConnection.INewConnectionHandler;
import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;
import kr.dja.plciot.Task.MultiThread.MultiThreadTaskOperator;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.TaskLock;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.TaskOption;
import kr.dja.plciot.UI.MainFrame;
import kr.dja.plciot.WebConnector.WebServer;
import kr.dja.plciot.WebConnector.WebServer.WebServerBuilder;
import kr.dja.plciot.WebIO.WebIOManager;

public class PLC_IoT_Core implements IMultiThreadTaskCallback
{
	private static PLC_IoT_Core MainInstance;
	public static final Console CONS = new Console();
	
	private final MainFrame mainFrame;
	private final DatabaseConnector dbManager;
	private final ConnectionManager connectionManager;
	private final DeviceManager deviceManager;
	private final DataflowDeviceLoader dataLoader;
	private final WebServer webServer;
	private final WebIOManager webIOManager;
	
	private PLC_IoT_Core()
	{//TEST
		this.mainFrame = new MainFrame();
		this.dbManager = new DatabaseConnector();
		this.connectionManager = new ConnectionManager();
		this.deviceManager = new DeviceManager(this.connectionManager, this.dbManager);
		this.dataLoader = new DataflowDeviceLoader(this.dbManager, this.deviceManager);
		this.webServer = new WebServer();
		this.webIOManager = new WebIOManager(this.webServer,this.dbManager, this.deviceManager);
		
		ConnectionManagerBuilder connectionManagerBuilder = new ConnectionManagerBuilder(this.connectionManager);
		connectionManagerBuilder.setReceiveRegister(this.connectionManager);
		
		WebServerBuilder webServerBuilder = new WebServerBuilder(this.webServer);
		
		MultiThreadTaskOperator serverStartOperator = new MultiThreadTaskOperator(TaskOption.START);
		
		serverStartOperator.addTask(this.dbManager);
		serverStartOperator.addTask(connectionManagerBuilder);
		serverStartOperator.addTask(this.deviceManager);
		serverStartOperator.addTask(this.dataLoader);
		serverStartOperator.addTask(webServerBuilder);
		serverStartOperator.addTask(this.webIOManager);
		serverStartOperator.addTask(this);
		//serverStartOperator.addTask(new PacketTestClass());
		
		MultiThreadTaskOperator serverShutdownOperator = new MultiThreadTaskOperator(TaskOption.SHUTDOWN);
		
		serverShutdownOperator.addTask(this.webIOManager);
		serverShutdownOperator.addTask(this.dataLoader);
		serverShutdownOperator.addTask(this.deviceManager);
		serverShutdownOperator.addTask(connectionManagerBuilder);
		serverShutdownOperator.addTask(webServerBuilder);
		serverShutdownOperator.addTask(this.dbManager);
		serverShutdownOperator.addTask(CONS);
		serverShutdownOperator.addTask(this.mainFrame);
		serverShutdownOperator.addTask(this);
		
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
	class PacketTestClass implements IMultiThreadTaskCallback
	{

		@Override
		public void executeTask(TaskOption option, NextTask nextTask)
		{
			TestReceiveHandler t = new TestReceiveHandler();
			
			connectionManager.addReceiveHandler(t);
			try
			{
				byte[] addr = new byte[]{(byte) 203,(byte) 250,(byte) 133,(byte) 159};
				connectionManager.startSendCycle(InetAddress.getByAddress(addr), 50011, "1A2B3C4D5E6E", "testPacket", "testData", t);
			}
			catch (UnknownHostException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	class TestReceiveHandler implements INewConnectionHandler, IPacketCycleUser
	{

		@Override
		public IPacketCycleUser createConnection(String uuid, String name)
		{
			System.out.println("createConnectionUID: " + uuid);
			System.out.println("createConnectionNAME: " + name);
			return this;
		}

		@Override
		public void packetSendCallback(boolean success, String name, String data)
		{
			
			
		}

		@Override
		public void packetReceiveCallback(InetAddress addr, String macAddr, String name, String data)
		{
			
			System.out.println("packetReceiveCallbackNAME: " + name);
			System.out.println("packetReceiveCallbackDATA: " + data);
		}
		
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
			next.nextTask();
		}
		if(option == TaskOption.SHUTDOWN)
		{
			CONS.push("서버 종료 완료.");
			System.exit(0);
		}
	}
}