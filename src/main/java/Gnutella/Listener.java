package Gnutella;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Listener implements Runnable {

    private static int LISTENING_PORT = 6346;
    public Connection connection;
    public static final String GREETING = "GNUTELLA CONNECT/0.4";
    public static final String READY = "GNUTELLA OK";
    public static final String BUSY = "GNUTELLA BUSY";
    public static byte[] greeting = (GREETING + "\n\n").getBytes();
    public static byte[] ready = (READY + "\n\n").getBytes();
    public static byte[] busy = (BUSY + "\n\n").getBytes();

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(LISTENING_PORT);

            while (true) {
                Socket socket = ss.accept();
                connection = new Connection(socket, Connection.INCOMING);

                String incoming = connection.getTextReader().readLine();
                if (incoming == null) {
                    continue;
                } else if (incoming.indexOf(GREETING) == -1) {
                    DownloadServer downloadserver = new DownloadServer(connection, incoming);
                    downloadserver.run();
                    continue;
                } else if (HostArray.getCount() >= Preferences.MAX_LIVE) {
                    connection.getTextReader().readLine(); // Gets rid of the extra newline
                    connection.getByteWriter().write(busy, 0, busy.length);
                    connection.getByteWriter().flush();
                } else if (((Mine.ipString).equals(connection.getIPAddress().toString())) && (Mine.port == connection.getIPAddress().getPort())) {
                    connection.getTextReader().readLine(); // Gets rid of the extra newline
                    connection.getByteWriter().write(busy, 0, busy.length);
                    connection.getByteWriter().flush();
                } else {
                    connection.getTextReader().readLine(); // Gets rid of the extra newline
                    connection.getByteWriter().write(ready, 0, ready.length);
                    connection.getByteWriter().flush();

                    Server server = new Server(connection);
                    ExecutorService threadExecutor = Executors.newCachedThreadPool();
                    threadExecutor.execute(server);
                    
                    
                    HostArray.addConnection(connection);
                    //HostCache.addConnection(connection);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
