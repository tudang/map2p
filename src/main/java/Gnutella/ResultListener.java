package Gnutella;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultListener implements Runnable{

    private static int LISTENING_PORT = 6347;
    public Connection connection;
    
    
    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(LISTENING_PORT);
            ExecutorService threadExecutor = Executors.newCachedThreadPool();
            while (true) {
                Socket socket = ss.accept();
                
                if(socket.isConnected()) {  
                ReceiveResult acon = new ReceiveResult(socket);
                threadExecutor.execute(acon);
                }
                
                
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
