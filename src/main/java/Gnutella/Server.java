package Gnutella;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

class Server extends Thread {

    BufferedInputStream in;
    IPAddress client;
    Connection cliconn;
    private static Logger logger = Logger.getLogger(Master.class);
    private static ExecutorService threadExecutor;
    public Server(Connection c) {
        cliconn = c;
        in = c.getByteReader();
        client = c.getIPAddress();
        threadExecutor = Executors.newFixedThreadPool(Preferences.numberThreads);
    }

    @Override
    public void run() {
        try {
            
            while (true) {

                if (in.available() < Packet.HEADER_LENGTH) {
                    continue;
                }
                byte[] temp = new byte[Packet.HEADER_LENGTH];

                in.read(temp, 0, Packet.HEADER_LENGTH);

                Packet header = new Packet(temp);

                if ((header.identify() != Packet.PONG) && (header.identify() != Packet.PING)
                        && (header.identify() != Packet.GROUP) && (header.identify() != Packet.JOIN)
                        && (header.identify() != Packet.MRINIT) && (header.identify() != Packet.MRFIN)
                        && (header.identify() != Packet.QUERY) && (header.identify() != Packet.QUERYHIT)
                        && (header.identify() != Packet.JOBFIN)) {
                    break;
                } else {

                    byte[] newpacket = new byte[(header.length() + Packet.HEADER_LENGTH)]; /*
                     * The syntax here is unfortunate, because headers don't
                     * store their own size.
                     */
                    header.decrementTtl();
                    header.incrementHops();
                    System.arraycopy(temp, 0, newpacket, 0, Packet.HEADER_LENGTH);


                    in.read(newpacket, Packet.HEADER_LENGTH, header.length());
                    if (header.getTtl() < 0) // Kill old packets (but only after we've removed them from the input stream).
                    {
                        continue;
                    }

                    if (header.identify() == Packet.PING) // We don't have to do any packet construction if all we've got is a Ping.
                    {
                        Ping ping = new Ping(newpacket);
                        PingHandler handler = new PingHandler(cliconn, ping);
                        handler.run();
                        continue;
                    }

                    if (header.identify() == Packet.PONG) {
                        Pong pong = new Pong(newpacket);
                        Host h = new Host(client.toString(), client.getPort());
                        HostCache.addHost(h);
                        PongHandler handler = new PongHandler(cliconn, pong);
                        handler.run();
                        Pinger.inform(pong);
                        continue;
                    } else if (header.identify() == Packet.QUERY) {
                        Query query = new Query(newpacket);
                        QHandler handler = new QHandler(cliconn, query);
                        handler.run();
                        continue;
                    } else if (header.identify() == Packet.QUERYHIT) {
                        QueryHit queryhit = new QueryHit(newpacket);
                        QHitHandler handler = new QHitHandler(client, queryhit);
                        handler.run();
                        Searcher.inform(client, queryhit);
                    } else if (header.identify() == Packet.GROUP) {
                        Group group = new Group(newpacket);
                        Worker.grouphandler(cliconn, group);
                    } else if (header.identify() == Packet.JOIN) {
                        Join join = new Join(newpacket);
                        Master.Joinhandler(cliconn, join);
                    } else if (header.identify() == Packet.MRINIT) {
                        Mrinit mrinit = new Mrinit(newpacket);
                        Worker atask = new Worker(mrinit);
                        threadExecutor.execute(atask);
                        
                        
                    } else if (header.identify() == Packet.MRFIN) {
                        Mrfin fin = new Mrfin(newpacket);
                           System.out.println("Received the result from " + client.toString());
                           Master.FinHandler(cliconn, fin);
                        
                    } else if (header.identify() == Packet.JOBFIN)  {
                                Worker.SendMyMap();
                    }
                }
            }
        } 
        catch (IOException ex) {
            logger.debug(ex.toString());
            System.out.println(ex.toString());
            NetworkManager.notify(cliconn);
            //HostCache.connectToHost(cliconn.getIPAddress().toString());

        }







        //   NetworkManager.notify(cliconn);
    }


}
