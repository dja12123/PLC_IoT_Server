package kr.dja.plciot.Web;

import kr.dja.plciot.Log.Console;
import kr.dja.plciot.Task.MultiThread.MultiThreadTaskOperator;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.TaskOption;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
 

public class WebServer implements Runnable, IMultiThreadTaskCallback
{
	public static final String ROOT_DOC = "/Users/kyoungil_lee/Desktop/web";

	private boolean stop = false;

	private final Console console;
	private Thread webServerThread;
	
	public WebServer(Console console)
	{
		this.console = console;
	}

	@Override
	public void run()
	{
		this.console.push("웹 서버 시작");
		ServerSocket serverSocket = null;
		int port = 80;
 
		try
		{
			serverSocket = new ServerSocket(port, 1);
		}
		catch (IOException ie)
		{
			ie.printStackTrace();
			System.exit(1);
		}
 
		while (!this.stop)
		{
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
 
			try
			{
				socket = serverSocket.accept();
				this.console.push("웹 서비스 요청");
				input = socket.getInputStream();
				output = socket.getOutputStream();
 
				Request request = new Request(input);
				request.parse();

				Response response = new Response(output);
				response.setRequest(request);
				response.sendStaticResource();
 
				socket.close();
 
				System.out.println(request.getUrl());
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				continue;
			}
		}
		this.console.push("서버 중단");
	}


	@Override
	public void executeTask(TaskOption option, MultiThreadTaskOperator operator)
	{
		if(option == TaskOption.START)
		{
			this.webServerThread = new Thread(this);
			this.webServerThread.start();
			operator.nextTask();
		}
		if(option == TaskOption.SHUTDOWN)
		{
			operator.nextTask();
		}
	}
}
