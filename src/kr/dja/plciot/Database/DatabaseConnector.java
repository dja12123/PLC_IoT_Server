package kr.dja.plciot.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import kr.dja.plciot.DependManager.DependencyTaskOperator;
import kr.dja.plciot.DependManager.IDependencyTask;
import kr.dja.plciot.DependManager.TaskOption;
import kr.dja.plciot.Log.Console;

public class DatabaseConnector implements IDependencyTask
{
	private static final String DB_ADDR = "203.250.133.158:3306";
	private static final String DB_NAME = "team_korea_server";
	private static final String DB_ID = "serverProgram";
	private static final String DB_PW = "thqkdzhfldk";
	
	private Console console;
	
	private Connection connection;
	private Statement statement;
	
	public DatabaseConnector(Console console)
	{
		this.console = console;
	}
	
	private boolean connectDB()
	{
		if(this.connection == null)
		{
			try
			{
				this.console.push("�����ͺ��̽� ���� �õ�");
				
				this.connection = DriverManager.getConnection("jdbc:mysql://"+DB_ADDR+"/"+DB_NAME, DB_ID, DB_PW);
				
				this.statement = connection.createStatement();
				
				ResultSet rs = this.statement.executeQuery("select version();");
				
				rs.next();
				this.console.push("�����ͺ��̽� ���� ���� - ����: " + rs.getString("version()"));
			}
			catch (SQLException e)
			{
				this.console.push("�����ͺ��̽� ���� ���� - " + e.toString());
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public void executeTask(TaskOption option, DependencyTaskOperator operator)
	{
		if(option == TaskOption.START)
		{
			new Thread(()->
			{
				while(!this.connectDB())
				{
					this.console.push("�����ͺ��̽� ���� ��õ� �����");
					try
					{
						Thread.sleep(6000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				operator.nextTask();
			}).start();
		}
		else if(option == TaskOption.SHUTDOWN)
		{
			this.console.push("�����ͺ��̽� ���� ������");
			if(this.connection != null)
			{
				try
				{
					this.connection.close();
					this.console.push("�����ͺ��̽� ���� ���� ����");
				}
				catch (SQLException e)
				{
					operator.error(e, "�����ͺ��̽� ����");
					this.console.push("�����ͺ��̽� ���� ���� ����");
				}
			}
			
			operator.nextTask();
		}
	}
}