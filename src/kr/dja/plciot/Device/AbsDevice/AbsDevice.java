package kr.dja.plciot.Device.AbsDevice;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kr.dja.plciot.Device.TaskManager.DeviceTaskHandler;
import kr.dja.plciot.LowLevelConnection.Cycle.IPacketCycleUser;

public abstract class AbsDevice implements IPacketCycleUser
{
	protected List<DeviceTaskHandler> taskManagerList;
	public final String macAddr;
	private boolean active;
	
	public AbsDevice(String macAddr)
	{
		this.taskManagerList = Collections.synchronizedList(new ArrayList<DeviceTaskHandler>());
		this.macAddr = macAddr;
		this.active = false;
	}
	
	public void activation()
	{
		this.active = true;
		for(DeviceTaskHandler taskManager : this.taskManagerList)
		{
			taskManager.registerDevice(this);
		}
	}
	
	public void deActivation()
	{
		this.active = false;
		for(DeviceTaskHandler taskManager : this.taskManagerList)
		{
			taskManager.deRegisterDevice(this);
		}
	}
	
	public boolean isActivation()
	{
		return this.active;
	}

	@Override
	public abstract void packetSendCallback(boolean success, String name, String data);

	@Override
	public abstract void packetReceiveCallback(InetAddress addr, String macAddr, String name, String data);


}
