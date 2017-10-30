package kr.dja.plciot.LowLevelConnection.Cycle;

import java.net.InetAddress;

import kr.dja.plciot.LowLevelConnection.PacketProcess;
import kr.dja.plciot.LowLevelConnection.PacketReceive.IPacketReceiveObservable;
import kr.dja.plciot.LowLevelConnection.PacketReceive.IPacketReceiveObserver;
import kr.dja.plciot.LowLevelConnection.PacketSend.IPacketSender;

public abstract class AbsCycle implements IPacketReceiveObserver
{
	protected final IPacketSender sender;
	protected final IPacketReceiveObservable receiver;
	private String fullUID;
	protected final InetAddress addr;
	private final IEndCycleCallback endCycleCallback;
	protected final IPacketCycleUser user;
	
	protected AbsCycle(IPacketSender sender, IPacketReceiveObservable receiver,
			 InetAddress addr, IEndCycleCallback endCycleCallback, IPacketCycleUser user)
	{
		this.sender = sender;
		this.receiver = receiver;
		this.addr = addr;
		this.endCycleCallback = endCycleCallback;
		this.user = user;
	}
	
	protected void startTask(String fullUID)
	{
		this.fullUID = fullUID;
		this.receiver.addObserver(this.fullUID, this);
	}
	
	public abstract void start();
	
	protected void notifyEndCycle()
	{
		this.receiver.deleteObserver(this.fullUID);
		this.endCycleCallback.endCycleCallback(this);
	}
	
	public static abstract class AbsCycleBuilder
	{
		protected IPacketSender sender;
		protected IPacketReceiveObservable receiver;
		protected InetAddress addr;
		protected IEndCycleCallback endCycleCallback;
		protected IPacketCycleUser user;
		
		public AbsCycleBuilder setSender(IPacketSender sender)
		{
			this.sender = sender;
			return this;
		}
		
		public AbsCycleBuilder setReceiver(IPacketReceiveObservable receiver)
		{
			this.receiver = receiver;
			return this;
		}
		
		public AbsCycleBuilder setAddress(InetAddress addr)
		{
			this.addr = addr;
			return this;
		}
		
		public AbsCycleBuilder setUserCallback(IPacketCycleUser user)
		{
			this.user = user;
			return this;
		}
		
		public AbsCycleBuilder setEndCycleCallback(IEndCycleCallback endCycleCallback)
		{
			this.endCycleCallback = endCycleCallback;
			return this;
		}
		
		public AbsCycleBuilder setUser(IPacketCycleUser user)
		{
			this.user = user;
			return this;
		}
	
		public abstract AbsCycle getInstance();
	}
}
