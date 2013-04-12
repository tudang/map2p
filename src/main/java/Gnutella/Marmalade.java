package Gnutella;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Marmalade {
    
  public static void main(String[] args) {
        
        QHandler.initQueryTable();
        PingHandler.initPingTable();
        
        Mine.updateAddress();
        Searcher searcher = new Searcher();
        
        Preferences.readFromFile();
        
      //  System.out.println("Setting up file table...");
      //  SharedDirectory sharedDirectory = new SharedDirectory(Preferences.SHAREPATH, Preferences.SAVEPATH);

        Listener listener = new Listener();
        ResultListener resultListener = new ResultListener();
        
        
        // Begin actively trying to connect
        PeriodicConnector periodicconnector = new PeriodicConnector(
                Preferences.AUTO_CONNECT);

        // Start sending out periodic pings.
        Pinger pinger = new Pinger();

        ExecutorService coreThreads = Executors.newCachedThreadPool();
        coreThreads.execute(listener);
        coreThreads.execute(periodicconnector);
        coreThreads.execute(resultListener);
        coreThreads.execute(pinger);
    }
}
