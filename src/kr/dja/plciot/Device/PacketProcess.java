package kr.dja.plciot.Device;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class PacketProcess
{// 장치와 주고받는 패킷에 관련된 함수와 상수를 캡슐화.
	
	private static final int BYTE = 8;// 8bit = byte
	
	private static final int FIELD_UUID = 4;// 0 ~ 3 Byte
	private static final int FIELD_MACADDR = 10;// 4 ~ 9 Byte
	private static final int FIELD_PHASE = 11; // 10 ~ 11 Byte
	private static final int FIELD_NAME = 32; // 12 ~ 32 Byte
	private static final int FIELD_TOTAL = 1024; // total 1024 Byte
	
	private static final byte DATAMAP_KEYVALUE = '=';
	private static final byte DATAMAP_NEXTKV = '\n';
	
	public static final byte PHASE_SEND = 0b00010110; // 0x16 SYN
	public static final byte PHASE_CHECK = 0b00000110; // 0x06 ACK
	public static final byte PHASE_EXECUTE = 0b00000101; // 0x05 ENQ
	
	private static final byte NULL_VALUE = 0b00000000;
	
	public static final int TIMEOUT = 2000;
	public static final int MAX_RESEND = 3;
	
	public static void SendDataByteCreation(String name, Map<String, String> sendData)
	{
		byte[] dataPacketByteFirst = new byte[FIELD_TOTAL];
		
		StringBuffer sendDataSerialStr = new StringBuffer();
		
		long time = System.currentTimeMillis();
		System.out.printf("%x\n", time);
		for(int i = 0; i < 4 * 8; i += 8)
		{
			System.out.printf("%x\n", (byte)time);
			time = time >> 8;
		}
		
		
		int dataPutIndex = 0;
		
		byte[] dataTypeNameByte = name.getBytes();
		for(int nameIndex = 0; nameIndex < FIELD_NAME; ++nameIndex)
		{
			if(dataTypeNameByte.length > nameIndex)
			{
				dataPacketByteFirst[dataPutIndex++] = dataTypeNameByte[nameIndex];
			}
			else
			{
				dataPacketByteFirst[dataPutIndex++] = NULL_VALUE;
			}
		}
		
		dataPacketByteFirst[dataPutIndex] = PHASE_SEND;		
		dataPutIndex += FIELD_PHASE;
		

		
	}
	
	public static byte[] createDataSet()
	{// 데이타 샛 생성.
		return new byte[FIELD_TOTAL];
	}
	
	public static byte[] createUUID()
	{// uuid 생성.
		byte[] uuid = new byte[FIELD_UUID];
		long time = System.currentTimeMillis();
		
		for(int uuidIndex = 0; uuidIndex < FIELD_UUID; ++uuidIndex)
		{
			uuid[uuidIndex] = (byte)time;
			time = time >> BYTE;
		}

		return uuid;
	}
	
	public static byte[] GetPacketUUID(byte[] packet)
	{// 패킷 uuid 를 가져옵니다.
		
		byte[] uuid = new byte[FIELD_UUID];
		
		for(int uuidIndex = 0; uuidIndex < FIELD_UUID; ++uuidIndex)
		{
			uuid[uuidIndex] = packet[uuidIndex];
		}
		
		return uuid;
	}

	public static byte[] GetpacketMacAddr(byte[] packet)
	{// 패킷 맥주소를 가져옵니다.
		
		byte[] macAddr = new byte[FIELD_MACADDR - FIELD_UUID];
		int macAddrIndex = 0;
		
		for(int macAddrPacket = FIELD_UUID; macAddrPacket < FIELD_MACADDR; ++macAddrPacket)
		{
			macAddr[macAddrIndex] = packet[macAddrPacket];
			++macAddrIndex;
		}
		
		return macAddr;
	}
	
	public static byte GetPacketPhase(byte[] packet)
	{// 패킷 페이즈를 가져옵니다.
		byte phase = packet[FIELD_PHASE - 1];
		return phase;				
	}
	
	public static Map<String, String> GetPacketData(byte[] dataByte)
	{// 데이터 바이트를 데이터 맵으로 디코드.
		
		Map<String, String> dataMap = new HashMap<String, String>();
		
		return dataMap;
	}
	
	public static byte[] dataMapToByte(Map<String, String> dataMap)
	{// 데이터 맵을 바이너리로 인코드.
		
		StringBuffer sendDataSerialStr = new StringBuffer();
		for(String key : dataMap.keySet())
		{
			String appendStr = key+DATAMAP_KEYVALUE+dataMap.get(key)+DATAMAP_NEXTKV;
			if(appendStr.length() + sendDataSerialStr.length() < FIELD_TOTAL - FIELD_NAME)
			{
				sendDataSerialStr.append(appendStr);
			}
			else
			{
				break;
			}
			
		}
		return sendDataSerialStr.toString().getBytes();
	}
	
	public static void InputPacketHeader(byte[] dataSet, byte[] uuid, byte[] macAddr, byte phase)
	{// 패킷 헤더를 만듭니다.
		
		int dataSetIndex = 0;
		
		while(dataSetIndex < FIELD_MACADDR)
		{// 신호 uuid 복사.
			dataSet[dataSetIndex] = uuid[dataSetIndex];
			++dataSetIndex;
		}
		
		for(int macAddrIndex = 0; dataSetIndex < FIELD_UUID; ++macAddrIndex)
		{// 신호 macAddr 복사.
			dataSet[dataSetIndex] = macAddr[macAddrIndex];
			++dataSetIndex;
		}
		
		dataSet[dataSetIndex] = phase; // 신호 페이즈 넣기 (index 10)
		
	}
	
	public static void InputPacketData(byte[] dataSet, String name, byte[] sendData)
	{// 데이타를 패킷 버퍼에 넣습니다.
		
		int dataSetIndex = FIELD_PHASE; // (index 11~)
		
		byte[] nameByte = name.getBytes();
		
		for(int dataNameIndex = 0; dataNameIndex < nameByte.length && dataSetIndex < FIELD_NAME; ++dataNameIndex)
		{
			dataSet[dataSetIndex] = nameByte[dataNameIndex];
			++dataSetIndex;
		}
		
		while(dataSetIndex <  FIELD_NAME)
		{
			dataSet[dataSetIndex] = NULL_VALUE;
			++dataSetIndex;
		}

		for(int sendDataPointer = 0; sendDataPointer < sendData.length && dataSetIndex < FIELD_TOTAL; ++sendDataPointer)
		{
			dataSet[dataSetIndex] = sendData[sendDataPointer];
			++dataSetIndex;
		}
		
		while(dataSetIndex < FIELD_TOTAL)
		{// 나머지 길이 모두 0으로 초기화.
			dataSet[dataSetIndex] = NULL_VALUE;
			++dataSetIndex;
		}
	}
	
}
