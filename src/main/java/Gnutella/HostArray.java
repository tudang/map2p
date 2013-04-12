package Gnutella;
/*
 * Author: Marmalade
 * Modified by Vu Ngoc Phach IT070096 - Nguyen Truong An IT070012
 */

import java.util.ArrayList;
import java.util.List;

public class HostArray {
    
    private static HostArray instance = new HostArray();
    private static List<Connection> connectedHosts;
    
    public static synchronized HostArray getInstance() {
        return instance;
    } 
    
    private HostArray() {
    connectedHosts = new ArrayList<Connection>();
    }
    
    public static synchronized int getCount()
    { 
	return (connectedHosts.isEmpty()) ? 0 : connectedHosts.size();
	
    }
    

    public static synchronized void addConnection(Connection c)
    {
	 connectedHosts.add(c);
    Searcher.updateAddedConnection(c);
      }
    

    public static synchronized void removeConnection(Connection c)
    {
	connectedHosts.remove(c);
        Searcher.updateRemovedConnection(c.getIPAddress());
    }
    
    public static synchronized void removeConnection(IPAddress ip)
    {
	for(Connection c:connectedHosts)  {
            if(c.getIPAddress().equals(ip)) {
            connectedHosts.remove(c);
            break;
            }
        }
    Searcher.updateRemovedConnection(ip);
    }
    
    public static synchronized Connection getConnection(int i)
    {
	if ((!connectedHosts.isEmpty()) && (i < connectedHosts.size()))
	    return connectedHosts.get(i);
	else return null;
    }
    
    public static synchronized Connection getConnection(IPAddress ip)
    {
	Connection conn = null;
	for ( Connection c : connectedHosts)
	    {
		if (c.getIPAddress().equals(ip)) {
                    conn = c;
                }
	    }
	return conn;
    }
    
    public static synchronized Connection getConnection(String ipaddr)
    {
	Connection conn = null;
	for ( Connection c : connectedHosts)
	    {
		if (c.getIPAddress().toString().equals(ipaddr)) {
                    conn = c;
                }
	    }
	return conn;
    }
  
    public static synchronized boolean isLive(Connection c)
    {
	 if (connectedHosts.isEmpty()) return false;
        else
        {
            for(Connection con: connectedHosts) {
                if(con.equals(c))
                    return true;
            }
            return false;
        }
    }
    
   
         
    public static synchronized boolean isLive(String ip)
    {
	 if (connectedHosts.isEmpty()) return false;
        else
        {
            for(Connection con: connectedHosts) {
                if(con.getIPAddress().toString().equals(ip))
                    return true;
            }
            return false;
        }
    }
}
