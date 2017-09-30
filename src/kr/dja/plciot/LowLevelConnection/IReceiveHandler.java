package kr.dja.plciot.LowLevelConnection;

import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;

public interface IReceiveHandler
{
	public IPacketCycleUser createConnection(String uuid, String name);
}
