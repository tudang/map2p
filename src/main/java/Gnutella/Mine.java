package Gnutella;

/*
  Lame way to get local IP address. Java's localHost method returns
  127.0.0.1; s this opens a socket to Sun' web server.
*/
import java.net.Socket;

public class Mine
{
  static  int port  = 6346;
  static  String  ipString = "";
  static  IPAddress ipObject = new IPAddress();
  static  byte[] serventID = new byte[16];
      
  public static void updateAddress()
  {
    try
    {
      Socket s = new Socket("www.google.com.vn", 80);
      ipString = s.getLocalAddress().getHostAddress();
      System.out.println("Local address: " + ipString);
      byte[] ipbytes = s.getLocalAddress().getAddress();
      ipObject = new IPAddress(ipbytes[0], ipbytes[1], ipbytes[2], ipbytes[3], port);
      for (int i = 0; i < 16; i++)
	  serventID[i] = ipbytes[i % 4];
      s.close();
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
  }

    public static IPAddress getIPAddress()
    {
      return ipObject;
    }
  
    public static byte[] getServentIdentifier()
    {
	return serventID;
    }
		    
  public static int getPort()
  {
    return port;
  }

    public static int getSpeed()
    {
	return (128);
    }

}

