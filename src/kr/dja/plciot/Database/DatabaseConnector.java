package kr.dja.plciot.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Log.Console;
import kr.dja.plciot.Task.MultiThread.MultiThreadTaskOperator;
import kr.dja.plciot.Task.MultiThread.NextTask;
import kr.dja.plciot.Task.MultiThread.IMultiThreadTaskCallback;
import kr.dja.plciot.Task.MultiThread.TaskOption;

public class DatabaseConnector implements IMultiThreadTaskCallback
{
	private static final String DB_ADDR = "203.250.133.158:3306";
	private static final String DB_NAME = "team_korea_server";
	private static final String DB_ID = "serverProgram";
	private static final String DB_PW = "thqkdzhfldk";
	
	private Connection connection;
	private Statement statement;
	
	public DatabaseConnector()
	{
		
	}
	
	private boolean connectDB()
	{
		if(this.connection == null)
		{
			try
			{
				PLC_IoT_Core.CONS.push("데이터베이스 연결 시도");
				
				this.connection = DriverManager.getConnection("jdbc:mysql://"+DB_ADDR+"/"+DB_NAME, DB_ID, DB_PW);
				
				this.statement = connection.createStatement();
				
				ResultSet rs = this.statement.executeQuery("select version();");
				
				rs.next();
				PLC_IoT_Core.CONS.push("데이터베이스 연결 성공 - 버전: " + rs.getString("version()"));
			}
			catch (SQLException e)
			{
				PLC_IoT_Core.CONS.push("데이터베이스 연결 실패 - " + e.toString());
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public void executeTask(TaskOption option, NextTask next)
	{
		if(option == TaskOption.START)
		{
			new Thread(()->
			{
				while(!this.connectDB())
				{
					PLC_IoT_Core.CONS.push("데이터베이스 연결 재시도 대기중");
					try
					{
						Thread.sleep(6000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				next.nextTask();
			}).start();
		}
		else if(option == TaskOption.SHUTDOWN)
		{
			PLC_IoT_Core.CONS.push("데이터베이스 접속 종료중");
			if(this.connection != null)
			{
				try
				{
					this.connection.close();
					PLC_IoT_Core.CONS.push("데이터베이스 접속 종료 성공");
				}
				catch (SQLException e)
				{
					next.error(e, "데이터베이스 오류");
					PLC_IoT_Core.CONS.push("데이터베이스 접속 종료 실패");
				}
			}
			
			next.nextTask();
		}
	}
}
