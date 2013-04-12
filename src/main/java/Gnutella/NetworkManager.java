package Gnutella;

/*
 * Network Manager - started by main
 */
import java.io.IOException;
import java.util.HashMap;

public class NetworkManager {

    public static void writeToOne(Connection connection, Packet packet) {
        if (HostArray.isLive(connection)) {
            
            try {
                connection.getByteWriter().write(packet.contents(), 0, packet.totalLength());
                connection.getByteWriter().flush();
            } catch (IOException e) {
                try {
                    connection.getSocket().close();
                    HostArray.removeConnection(connection);
                } catch (IOException exception) {
                    System.err.println(exception.getMessage());
                }
            }
        }
    }

    public static void writeToAll(Packet packet) {
        int size = HostArray.getCount();
        HashMap hm = new HashMap(size);
        for (int i = 0; i < size; i++) {
            Connection c = HostArray.getConnection(i);
            String ip = c.getIPAddress().toString();
            if (hm.containsKey(ip)) {
                break;
            } else {
                hm.put(ip, 1);

                try {
                    c.getByteWriter().write(packet.contents(), 0, packet.totalLength());
                    c.getByteWriter().flush();
                } catch (IOException e) {

                    try {
                        c.getSocket().close();
                        HostArray.removeConnection(c);

                    } catch (IOException exception) {
                       System.err.println(exception.getMessage());
                    }
                }
            }
        }
    }

    public static void writeButOne(Connection myip, Packet packet) {
        int size = HostArray.getCount();
        HashMap hm = new HashMap(size);
        hm.put(myip.getIPAddress().toString(), 1);
        for (int i = 0; i < size; i++) {
            Connection c = HostArray.getConnection(i);
            String ip = c.getIPAddress().toString();
            if (hm.containsKey(ip)) {
                break;
            } else {
                hm.put(ip, 1);
            
                try {
                    c.getByteWriter().write(packet.contents(), 0, packet.totalLength());
                    c.getByteWriter().flush();
                } catch (IOException e) {
                    try {
                        c.getSocket().close();
                        HostArray.removeConnection(c);
                    } catch (IOException exception) {
                        System.err.println(exception.getMessage());
                    }
                }
            
        }
      }
    }

    public static void notify(Connection conn) // Remove socket from open connection list, based on its IP.
    {
        if (HostArray.isLive(conn)) {
            System.out.println("Killing " + conn.getIPAddress().toString());
          try {
                conn.getSocket().close();
                HostArray.removeConnection(conn);
            } catch (IOException exception) {
               System.err.println(exception.getMessage());
            }
        }
    }
}
