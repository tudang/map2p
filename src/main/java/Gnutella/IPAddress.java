package Gnutella;

import java.util.StringTokenizer;

public class IPAddress
{
    int ip1 = 0;
    int ip2 = 0;
    int ip3 = 0;
    int ip4 = 0;
    int port = 0;
    
    public IPAddress() {}
    
    public IPAddress(int ip1, int ip2, int ip3, int ip4, int port)
    {
	this.ip1 = ip1;
	this.ip2 = ip2;
	this.ip3 = ip3;
	this.ip4 = ip4;
	this.port = port;
    }
    
    public IPAddress(String hostname, int port)
    {
	StringTokenizer st = new StringTokenizer(hostname, ".");
        
        while(st.hasMoreTokens()) {
          ip1 = Integer.parseInt(st.nextToken());
          ip2 = Integer.parseInt(st.nextToken());
          ip3 = Integer.parseInt(st.nextToken());
          ip4 = Integer.parseInt(st.nextToken());
        }
	this.port = port;
    }

    @Override
  public String toString()
  {
    return (ip1 + "." + ip2 + "." + ip3 + "." + ip4);
  }
  
    public int getFirst()
    {
	return (ip1);
    }

    public int getSecond()
    {
	return (ip2);
    }

    public int getThird()
    {
	return (ip3);
    }

    public int getFourth()
    {
	return (ip4);
    }

    public int getPort()
    {
	return (port);
    }
     
    @Override
    public boolean equals(Object ip)
    {
        IPAddress compare = (IPAddress) ip;
	return ((ip1 == compare.ip1) && (ip2 == compare.ip2) && (ip3 == compare.ip3) && (ip4 == compare.ip4) && (port == compare.port));
    }


}
