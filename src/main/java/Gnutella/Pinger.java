package Gnutella;

public class Pinger implements Runnable {

    static int hosts = 0;
    static int totalkb = 0;
    static int totalfiles = 0;
    static Ping myping;

    public Pinger() {
    myping = new Ping();
    }

    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(Preferences.PINGER_TIME);
                Searcher.updateInfo(hosts, totalkb, totalfiles);
                
                myping.setIP(Mine.getIPAddress());
                hosts = 0;
                totalkb = 0;
                totalfiles = 0;
                NetworkManager.writeToAll(myping);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    public static void inform(Pong pong) {
        if (pong.compare(myping)) {
            hosts++;
            totalfiles += pong.getNumFiles();
            totalkb += pong.getKb();
        }
    }
}
