package kr.dja.plciot.Device.AbsDevice.DataFlow;

import java.net.InetAddress;
import java.util.Map;

import kr.dja.plciot.PLC_IoT_Core;
import kr.dja.plciot.Device.AbsDevice.AbsDevice;
import kr.dja.plciot.Device.TaskManager.RealTimeDataHandler;
import kr.dja.plciot.LowLevelConnection.PacketProcess;

public class DeviceConsent extends AbsDataFlowDevice
{
	public static final String TYPE_NAME = "CONSENT";
	
	int power;
	int humi;
	int temp;
	int bright;
	
	public DeviceConsent(String macAddr, RealTimeDataHandler realTimeDataHandler)
	{
		super(macAddr, realTimeDataHandler);
		// TODO Auto-generated constructor stub
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

	@Override
	public void getDeviceValues(Map<String, Integer> map)
	{
		map.put("Power", power);
		map.put("Humi", humi);
		map.put("Temp", temp);
		map.put("Bright", bright);
	}

	@Override
	protected void storeValue(String data)
	{
		String[] dataSplit = data.split(PacketProcess.DEFAULT_SPLIT_REGEX);
		this.power = Integer.parseInt(dataSplit[0]);
		this.humi = Integer.parseInt(dataSplit[1]);
		this.temp = Integer.parseInt(dataSplit[2]);
		this.bright = Integer.parseInt(dataSplit[3]);
		System.out.println("스위치 데이터 적재 완료");
	}

}
