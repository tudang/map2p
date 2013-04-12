package Gnutella;


/**Similar to QHitHandler, PongHandler will process pongs that match pings forwarded
   by us, not pings that originated with Pinger.*/

class PongHandler 
{
    Pong pong;
    IPAddress pingIP;
    Connection ip;
    Ping pingMatch;

    public PongHandler (Connection ip, Pong pong)
    {
	this.pong = pong;
	this.ip = ip;
    }

    public void run()
    {
	String ipname = pong.getIP().toString();
	int port = pong.getPort();

	Host newhost = new Host(ipname, port);
	HostCache.addHost(newhost);

	if (PingHandler.pt.containsKey(pong))
	    {
		pingMatch = (Ping) PingHandler.pt.get((Packet) pong); /**Matching pong is used as key to obtain original ping.*/
		pingIP = pingMatch.getIP();
		NetworkManager.writeToOne (ip, pong);
	    }
    }
}



