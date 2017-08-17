package kr.dja.plciot.Device.SendResiveManage;

public interface IPacketObserver
{
	public void packetResive(byte[] resiveData);
}
