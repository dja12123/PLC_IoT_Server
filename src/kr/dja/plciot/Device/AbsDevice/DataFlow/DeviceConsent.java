package kr.dja.plciot.Device.AbsDevice.DataFlow;

import java.net.InetAddress;
import java.util.Map;

import kr.dja.plciot.Database.IDatabaseHandler;
import kr.dja.plciot.Device.IDeviceEventObserver;
import kr.dja.plciot.LowLevelConnection.ISendCycleStarter;
import kr.dja.plciot.LowLevelConnection.PacketProcess;

public class DeviceConsent extends AbsDataFlowDevice
{
	public static final String TYPE_NAME = "CONSENT";
	
	int power;
	int humi;
	int temp;
	int bright;
	
	public DeviceConsent(String macAddr, ISendCycleStarter sendManager, 
			IDeviceEventObserver eventObserver, IDatabaseHandler dbhandler)
	{
		super(macAddr,sendManager, eventObserver, dbhandler);
	}

	@Override
	public void packetSendCallback(boolean success, String name, String data)
	{
		super.packetSendCallback(success, name, data);
		
	}

	@Override
	public void packetReceiveCallback(InetAddress addr, String macAddr, String name, String data)
	{
		super.packetReceiveCallback(addr, macAddr, name, data);
	}

	public int getDeviceValue(String key)
	{
		System.out.println("장치 값 가져오기 " + key);
		switch(key)
		{
		case "Power":
			return this.power;
		case "Humi":
			return this.humi;
		case "Temp":
			return this.temp;
		case "Bright":
			return this.bright;
		}
		return -1;
	}

	@Override
	protected void storeValue(String data)
	{
		String[] dataSplit = data.split(PacketProcess.DEFAULT_SPLIT_REGEX);
		this.power = Integer.parseInt(dataSplit[0]);
		this.humi = Integer.parseInt(dataSplit[1]);
		this.temp = Integer.parseInt(dataSplit[2]);
		this.bright = Integer.parseInt(dataSplit[3]);
	}
}
