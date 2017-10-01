package kr.dja.plciot.Database;

import java.sql.ResultSet;

public interface IDatabaseHandler
{
	public int sqlUpdate(String sql);
	public ResultSet sqlQuery(String sql);
}
