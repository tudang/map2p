package Gnutella;

/*
 * Tries to connect periodically to the hosts int the HostCache
 */
public class PeriodicConnector implements Runnable {
    
    private static boolean execute;
    
    public PeriodicConnector(boolean execute) {
        PeriodicConnector.execute = execute;
    }
    
    @Override
    public void run() {
        while (true) {
            if ((HostArray.getCount() > Preferences.MAX_LIVE) || !execute) {
                continue;
            }
            
            for (int i = 0; i < HostCache.getCount(); i++) {
                String ipString = HostCache.getIP(i);
                  if (!(HostArray.isLive(ipString))) {
                    HostCache.connectHost(i);
                }   
                }
                
                try {
                    Thread.sleep(Preferences.CONNECTOR_TIME);
                } catch (Exception e) {
                    System.out.println(e.getMessage());                }
            }
        
    }
    
    public static void turnOn() {
        execute = true;
    }
    
    public static void turnOff() {
        execute = false;
    }
}
