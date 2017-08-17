package kr.dja.plciot.Web;

import java.io.IOException;
import java.io.InputStream;
 
/**
 * Created by kyoungil_lee on 14. 5. 20..
 */
public class Request
{
	private InputStream input;
	private String url;
 
	Request (InputStream input)
	{
		this.input = input;
	}
 
	public void parse ()
	{
		StringBuffer requestBuffer = new StringBuffer(2048);
		int i;
		byte[] buffer = new byte[2048];
 
		try
		{
			i = input.read(buffer);
		}
		catch (IOException ie)
		{
			ie.printStackTrace();
			i = -1;
		}
 
		for (int j=0; j<i; j++)
		{
			requestBuffer.append((char)buffer[j]);
		}
		
		url = parseUri(requestBuffer.toString());
	}
 
	private String parseUri (String requestString)
	{
		int index1, index2; 
		index1 = requestString.indexOf(" ");

		if (index1 != 1)
		{
			index2 = requestString.indexOf(" ", index1 + 1);
 
			if (index2 > index1)
			{
				return requestString.substring(index1 + 1, index2);
			}
		}
 
		return null;
	}
 
	public String getUrl ()
	{
		return this.url;
	}
}