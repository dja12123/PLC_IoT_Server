package kr.dja.plciot.Device.SendResiveManage;

public interface IPacketObservable
{
	public void addObserver(byte[] macAddr, IPacketObserver o);
	public void deleteObserver(IPacketObserver o);
}
