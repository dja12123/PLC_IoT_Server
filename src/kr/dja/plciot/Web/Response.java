package kr.dja.plciot.Web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kyoungil_lee on 14. 5. 20..
 */
public class Response
{
    private static final int BUFFER_SIZE = 1024;
    private Request request;
    private OutputStream output;

    Response (OutputStream output)
    {
        this.output = output;
    }

    public void setRequest (Request request)
    {
        this.request = request;
    }

    public void sendStaticResource () throws IOException
    {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;

        try
        {
        	output.write("»Æ¿Œ".getBytes());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }
    }
}