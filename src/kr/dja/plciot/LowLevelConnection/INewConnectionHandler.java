package kr.dja.plciot.LowLevelConnection;

import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;

public interface INewConnectionHandler
{
	public IPacketCycleUser createConnection(String uuid, String name);
}
