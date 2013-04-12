package Gnutella;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Connector {

    public String GREETING = "GNUTELLA CONNECT/0.4";
    public String READY = "GNUTELLA OK";
    public byte[] greeting = (GREETING + "\n\n").getBytes();
    public byte[] ready = (READY + "\n\n").getBytes();
    private String host;
    private int port;
    private static int TIMEOUT = 10000;
    private Connection connection;

    // Constructor for making a connection to a servent
    public Connector(String aHost, int aPort, int t) {
        host = aHost;
        port = aPort;
        TIMEOUT = t;

    }

    public Connector(String aHost, int aPort) {
        this(aHost, aPort, TIMEOUT);
    }

    public void run() {
        String incoming = null;
        try {
            //Socket socket = SocketMaker.makeSocket(aHost, aPort, t);
            InetAddress saddr = InetAddress.getByName(host);
            SocketAddress socketAddress = new InetSocketAddress(saddr,port);
            Socket socket   = new Socket();
            socket.connect(socketAddress, TIMEOUT);
            socket.setKeepAlive(true);
            connection = new Connection(socket, Connection.OUTGOING);

            connection.getByteWriter().write(greeting, 0, greeting.length);
            connection.getByteWriter().flush();
            incoming = connection.getTextReader().readLine();
        } catch (IOException e) {
           //System.err.println("Can't connect to " + host);
           
        }


        if (incoming == null || incoming.indexOf(READY) == -1) {
           
        } else {
            System.out.println("Connected to " + host);
            HostArray.addConnection(connection);
            Host h = new Host(connection.getIPAddress().toString(), connection.getIPAddress().getPort());
            HostCache.addHost(h);
            Server server = new Server(connection);
            server.start();
        }

    }
}
