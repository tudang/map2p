package Gnutella;

import java.util.Hashtable;
import java.util.Map;

class PingHandler 
{
    public static Map pt;  //ping table    
    Ping ping;
    Connection pingIP;
    public PingHandler (Connection pingIP, Ping ping)
    {
	this.ping = ping;
	this.pingIP = pingIP;
    }

    public static void initPingTable()
    {
	pt = new Hashtable (5000);
    }

    public void run()
    { //System.out.println("Received Ping ");
	if (!pt.containsKey(ping))  //check that ping is not already in table
	    {   
		NetworkManager.writeButOne(pingIP, ping);
		pt.put ((Packet) ping, ping);
		Pong response = new Pong(Mine.getPort(), Mine.getIPAddress(), SharedDirectory.getOurNumFiles(),
					 SharedDirectory.getOurKb(), ping.getMessageID());
		NetworkManager.writeToOne(pingIP, response);
	    }
    }
}


