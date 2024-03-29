package Gnutella;

/**
   Host has a string IP address and int port
*/

public class Host 
{
  private String hostName;
  private int hostPort;
    
  public Host(String aHostName, int aHostPort)
  {
    hostName = aHostName;
    hostPort = aHostPort;
  }
  
  public String getName()
  {
    return hostName;
  }
  
  public int getPort()
  {
    return hostPort;
  }

  @Override
  public boolean equals(Object aHost)
  {
      Host h = (Host) aHost;
    return ((hostName.equals(h.hostName)) );
  }
}
