package kr.dja.plciot.LowLevelConnection;

import kr.dja.plciot.LowLevelConnection.Cycle.CycleProcess;

public class PacketProcess
{// 장치와 주고받는 패킷에 관련된 함수와 상수를 캡슐화.

	public static void main(String args[])
    {
		String macAddr = "CAFEBABE0000";
		String fulluid = PacketProcess.CreateFULLUID(macAddr);
		String packetName = "testPacket";
		String sendData = "abcdefghijklmnopqrstuvwxyz";
		
		byte[] header = PacketProcess.CreatePacketHeader(fulluid);
		byte[] packet = PacketProcess.CreateFullPacket(header, packetName, sendData);
		
		System.out.println(PacketProcess.CheckPacket(packet));
		PacketProcess.PrintDataPacket(packet);
    }
	
	private static final int BYTE = 8;// 8bit = byte
	
	private static final int FIELD_TIMEID_START = 0;// 0 ~ 3 Byte
	private static final int FIELD_FULLUID_START = FIELD_TIMEID_START;
	private static final int FIELD_TIMEID_END = 3;// 0 ~ 3 Byte
	private static final int FIELD_MACADDR_START = 4;// 4 ~ 9 Byte
	private static final int FIELD_MACADDR_END = 9;// 4 ~ 9 Byte
	private static final int FIELD_FULLUID_END = FIELD_MACADDR_END;
	private static final int FIELD_PHASE = 10; // 10 Byte
	private static final int PACKET_HEADER_SIZE = FIELD_PHASE + 1;
	
	private static final int PACKET_INFO_SIZE = 2;
	private static final int FIELD_NAME_SIZE = 11; // 11 Byte
	private static final int FIELD_DATA_SIZE = 12; // 12 Byte
	
	private static final int FIELD_NAMEDATA_SIZE = 13; // 13 ~~
	
	private static final int SIZE_TIME = FIELD_TIMEID_END - FIELD_TIMEID_START + 1;
	private static final int SIZE_MACADDR = FIELD_MACADDR_END - FIELD_MACADDR_START + 1;
	
	private static final byte NULL_VALUE = 0b00000000;
	
	public static final int MAX_PACKET_LENGTH = (Byte.MAX_VALUE * 2) + PACKET_HEADER_SIZE + PACKET_INFO_SIZE;
	
	public static final String DEFAULT_SPLIT_REGEX = "/";
	
	public static void PrintDataPacket(byte[] packet)
	{
		System.out.print("HEX: ");
		for(byte bin : packet)
		{
			System.out.print(String.format("%02X", bin) + ' ');
		}
		System.out.println();
		System.out.println("PacketSize - " + PacketProcess.GetPacketSize(packet) + '(' + packet.length + ')');
		System.out.println("FullUID    - " + PacketProcess.GetPacketFULLUID(packet));
		System.out.println("MacAddr    - " + PacketProcess.GetpacketMacAddr(packet));
		System.out.println("Phase      - " + String.format("%02X", PacketProcess.GetPacketPhase(packet)));
		System.out.println("PacketName - " + PacketProcess.GetPacketName(packet));
		System.out.println("PacketData - " + PacketProcess.GetPacketData(packet));
	}
	
	public static boolean ComparePacket(byte[] a, byte[] b)
	{
		if(a.length != b.length) return false;
		for(int i = 0; i < a.length; ++i)
		{
			if(a[i] != b[i])
			{
				if(i == FIELD_PHASE)
				{
					continue;
				}
				return false;
			}
		}
		return true;
	}
	
	public static boolean CheckPacket(byte[] packet)
	{
		if(packet.length == PACKET_HEADER_SIZE)
		{
			return true;
		}
		
		if(packet.length > PACKET_HEADER_SIZE)
		{
			return CheckFullPacket(packet);
		}
		
		return false;
	}
	
	public static boolean CheckFullPacket(byte[] packet)
	{
		if(packet.length < FIELD_NAMEDATA_SIZE)
		{
			return false;
		}
		
		if(packet.length > MAX_PACKET_LENGTH)
		{
			return false;
		}
		
		if(packet[FIELD_NAME_SIZE] < 0)
		{
			return false;
		}
		
		if(packet[FIELD_DATA_SIZE] < 0)
		{
			return false;
		}
		
		if(packet[FIELD_NAME_SIZE] + packet[FIELD_DATA_SIZE] != packet.length - FIELD_NAMEDATA_SIZE)
		{
			return false;
		}
		return true;
	}
	
	public static String CreateFULLUID(String macAddr)
	{// uuid 생성.
		StringBuffer returnUUID = new StringBuffer();
		long time = System.currentTimeMillis();
		
		for(int uuidTime = 0; uuidTime < SIZE_TIME; ++uuidTime)
		{
			returnUUID.append(String.format("%02X", (byte)time));
			time = time >> BYTE;
		}
		
		for(int uuidMac = 0; uuidMac < SIZE_MACADDR; ++uuidMac)
		{
			returnUUID.append(macAddr.substring(uuidMac * 2, (uuidMac * 2) + 2));
		}
		
		return returnUUID.toString();
	}
	
	public static byte[] CreatePacketHeader(String fullUID)
	{// 패킷 헤더를 생성.
		
		byte[] dataSet = new byte[PACKET_HEADER_SIZE];
		
		int dataSetIndex = 0;
		for(int uuidIndex = FIELD_FULLUID_START; uuidIndex <= FIELD_FULLUID_END; ++uuidIndex)
		{
			dataSet[dataSetIndex++] = (byte)Integer.parseInt(fullUID.substring(uuidIndex * 2, (uuidIndex * 2) + 2), 16);
		}
		dataSet[FIELD_PHASE] = NULL_VALUE; // 신호 페이즈 넣기 (index 10)
		
		return dataSet;
	}
	
	public static void SetPacketPhase(byte[] packet, byte phase)
	{
		packet[FIELD_PHASE] = phase;
	}
	
	public static byte[] CreateFullPacket(byte[] header, String name, String data)
	{// 헤더 정보를 합친 데이터 페킷을 생성합니다
		
		int index = 0;
		
		if(name.length() > Byte.MAX_VALUE)
		{
			new Exception("name is too long");
		}
		if(data.length() > Byte.MAX_VALUE)
		{
			new Exception("data is too long");
		}
		
		byte nameFieldSize = (byte)(name.length());
		byte dataFieldSize = (byte)(data.length());
		
		byte[] packet = new byte[PACKET_HEADER_SIZE + nameFieldSize + dataFieldSize + PACKET_INFO_SIZE];
		
		for(int headerIndex = 0; headerIndex < PACKET_HEADER_SIZE; ++headerIndex)
		{
			packet[index++] = header[headerIndex];
		}
		
		packet[index++] = nameFieldSize;
		packet[index++] = dataFieldSize;
		
		for(int nameIndex = 0; nameIndex < name.length(); ++nameIndex)
		{
			packet[index++] = (byte)name.charAt(nameIndex);
		}
		
		for(int dataIndex = 0; dataIndex < data.length(); ++dataIndex)
		{
			packet[index++] = (byte)data.charAt(dataIndex);
		}
		
		return packet;
	}
	
	public static String GetPacketFULLUID(byte[] packet)
	{// 패킷 uuid 를 가져옵니다.
		
		StringBuffer fullUID = new StringBuffer();
		
		for(int uuidIndex = FIELD_FULLUID_START; uuidIndex  <= FIELD_FULLUID_END; ++uuidIndex)
		{
			fullUID.append(String.format("%02X", packet[uuidIndex]));
		}
		
		return fullUID.toString();
	}

	public static String GetpacketMacAddr(byte[] packet)
	{// 패킷 맥주소를 가져옵니다.
		StringBuffer macAddr = new StringBuffer();
		
		for(int macAddrPacket = FIELD_MACADDR_START; macAddrPacket <= FIELD_MACADDR_END; ++macAddrPacket)
		{
			macAddr.append(String.format("%02X", packet[macAddrPacket]));
		}
		
		return macAddr.toString();
	}
	
	public static byte GetPacketPhase(byte[] packet)
	{// 패킷 페이즈를 가져옵니다.
		byte phase = packet[FIELD_PHASE];
		return phase;				
	}
	
	public static String GetPacketName(byte[] packet)
	{// 패킷 이름을 가져옵니다.
		StringBuffer packetName = new StringBuffer();
		int index = FIELD_NAMEDATA_SIZE;
		
		byte nameSize = packet[FIELD_NAME_SIZE];
		
		// null 문자 제거.
		for(int nameIndex = 0; nameIndex < nameSize; ++nameIndex)
		{
			packetName.append((char)packet[index++]);
		}
		
		return packetName.toString();
	}
	
	public static String GetPacketData(byte[] packet)
	{// 패킷 데이터를 가져옵니다.
		StringBuffer packetData = new StringBuffer();
		int index = FIELD_NAMEDATA_SIZE + packet[FIELD_NAME_SIZE];
		
		byte dataSize = packet[FIELD_DATA_SIZE];
		
		// null 문자 제거.
		for(int dataIndex = 0; dataIndex < dataSize; ++dataIndex)
		{
			packetData.append((char)packet[index++]);
		}
		
		return packetData.toString();
	}

	public static int GetPacketSize(byte[] packet)
	{
		int packetSize = PACKET_HEADER_SIZE + PACKET_INFO_SIZE;
		packetSize += packet[FIELD_NAME_SIZE];
		packetSize += packet[FIELD_DATA_SIZE];
		
		return packetSize;
	}
	
	/*public static Map<String, String> GetPacketData(byte[] dataByte)
	{// 데이터 바이트를 데이터 맵으로 디코드.
		Map<String, String> dataMap = new HashMap<String, String>();
		
		StringBuffer strBuf = new StringBuffer();
		String key = null, value = null;
		
		for(int dataIndex = FIELD_NAME; dataIndex < FIELD_TOTAL; ++dataIndex)
		{
			if(dataByte[dataIndex] == DATAMAP_SYNTEX_KEY)
			{
				key = strBuf.toString();
				strBuf.setLength(0);
			 	continue;
			}
			else if(dataByte[dataIndex] == DATAMAP_SYNTEX_VALUE)
			{
				value = strBuf.toString();
				strBuf.setLength(0);
				
				dataMap.put(key, value);
				continue;
			}
			strBuf.append((char)dataByte[dataIndex]);
			
		}
		return dataMap;
	}*/
	
	/*public static byte[] DataMapToByte(Map<String, String> dataMap)
	{// 데이터 맵을 바이너리로 인코드.
		
		StringBuffer sendDataSerialStr = new StringBuffer();
		for(String key : dataMap.keySet())
		{
			String appendStr = key+String.valueOf((char)DATAMAP_SYNTEX_KEY)+dataMap.get(key)+String.valueOf((char)DATAMAP_SYNTEX_VALUE);
			
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
	}*/
	
	
}
