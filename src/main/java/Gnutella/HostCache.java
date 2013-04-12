package Gnutella;

import java.util.ArrayList;
import java.util.List;

/*
  Host cache - list of IP addresses to load on start up
*/

public class HostCache
{
  public static List<Host> hosts = new ArrayList<Host>();
  
   
  public static synchronized int getCount()
  {
    return (hosts.isEmpty()) ? 0 : hosts.size();
    
  }
  
  public static String getIP(int i)
  {
    return hosts.get(i).getName();
  }
  
  public static int getPort(int i)
  {
    return hosts.get(i).getPort();
  }

  

  public static void addConnection(Connection c)
  {
    Host h = new Host(c.getIPAddress().toString(), c.getIPAddress().getPort());
    addHost(h);
  }
  
  public static synchronized void addHost(Host h)
  {
    if (!hosts.contains(h))
	  {
      hosts.add(h);
      System.out.println("Host added in HostCache " + h.getName() + " total hosts " + getCount());
      Searcher.updateHostCache(h, true);
 	  }
  }

  public static void removeConnection(Connection c)
  {
    Host h = new Host(c.getIPAddress().toString(), c.getIPAddress().getPort());
    removeHost(h);
  }

  public static synchronized void removeHost(Host h)
  {
    if (!hosts.isEmpty() && hosts.contains(h))
    {
      hosts.remove(h);
      System.out.println("Host removed in HostCache " + h.getName() + " Hosts left " + getCount());
      Searcher.updateHostCache(h, false);
    }
  }



  public static void connectHost(int i)
  {
    if (!hosts.isEmpty() )
    {
      Connector connector = new Connector(getIP(i), getPort(i), 10000);
      connector.run();
    }
  }
  
  public static void connectToHost(String ip)
  {
    if (!hosts.isEmpty() )
    {
      //System.out.println("Attempting to connect to " + ip );
      Connector connector = new Connector(ip, 6346, 10000);
      connector.run();
    }
  }
}





    
      
