package kr.dja.plciot.DeviceConnection.Cycle;

public class CycleProcess
{
	public static final byte PHASE_START = 0b00010110;
	public static final byte PHASE_CHECK = 0b00000110;
	public static final byte PHASE_EXECUTE = 0b00000101;
	
	public static final int TIMEOUT = 2000;
	public static final int MAX_RESEND = 3;
}
