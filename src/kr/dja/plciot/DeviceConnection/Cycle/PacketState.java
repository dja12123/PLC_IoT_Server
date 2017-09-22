package kr.dja.plciot.DeviceConnection.Cycle;
public enum PacketState
{
	START((byte)0b00010110), CHECK((byte)0b00000110), EXECUTE((byte)0b00000101);
	// 0x16 SYN, 0x06 ACK, 0x05 ENQ
	
	public final byte code;
	
	private PacketState(byte b)
	{
		this.code = b;
	}
}
