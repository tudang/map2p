/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author dhtu
 */
public class Worker implements Runnable {

    private static int groupID;
    private static Connection serverCon;
    ExecutorService ftpthread;
    private Mrinit mytask;
    private static ConcurrentHashMap<String, Integer> partHashMap;
    private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private StringBuffer strBuf = new StringBuffer();
    private String inputFile;
    private String inputPath;
    private static Logger logger = Logger.getLogger(Worker.class);

    public Worker(Mrinit mrinit) {
        mytask = mrinit;
        inputPath = mytask.getPath();
        ftpthread = Executors.newCachedThreadPool();
       
    }

    public static void grouphandler(Connection serverConnection, Group group) {
        serverCon = serverConnection;
        setGroup(group.getGroupID());
        partHashMap = new ConcurrentHashMap<String, Integer>(65535);

        Searcher.updateGroupJoin(groupID);
        IPAddress currentIP = Mine.getIPAddress();
        Join join = new Join(currentIP.getPort(), currentIP, groupID);
        //   System.out.println("send message to "+ serverIP + 
        //          " my IP " +Mine.getIPAddress().toString() + " my port " + Mine.getPort());
        NetworkManager.writeToOne(serverConnection, join);
    }

    public static void setGroup(int gid) {
        groupID = gid;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        try {
            /*
             * ------------------------download DATA-------------------
             */
            
            downloadinput();
            Date date2 = new Date();
            strBuf.append(String.format("DownloadFinish\t%s\n",dateFormat.format(date2).toString()));
            /*
             * ------------------------Finish download DATA-------------------
             */
            /*
             * ------------------------Run WordCount-------------------
             */
            // System.out.println("Run WordCount");
            HashMap<String, Integer> hashMap = new HashMap<String, Integer>(65536);
            in = new BufferedReader(new FileReader(Preferences.SAVEPATH + File.separator + inputFile));
            String str;
            while ((str = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str);
                while (st.hasMoreTokens()) {
                    String key = st.nextToken();

                    if (hashMap.containsKey(key)) {
                        Integer value = (Integer) hashMap.get(key);
                        hashMap.put(key, new Integer(value + 1));
                    } else {
                        hashMap.put(key, new Integer(1));
                    }
                }
            }
            Date date3 = new Date();
            strBuf.append(String.format("FinishMR\t%s\n",dateFormat.format(date3).toString()));
            MergeHashMap(hashMap);
            /*
             * ------------------------End Run WordCount-------------------
             */
            /*
             * ------------------------Send Result-------------------
             */
            // System.out.println("Sending Notification");
            Mrfin fin = new Mrfin(mytask.getTaskID(), mytask.getTask());
            NetworkManager.writeToOne(serverCon, fin);
            in.close();
            Date date4 = new Date();
            strBuf.append(String.format("SentNotification\t%s\n",dateFormat.format(date4).toString()));
            /*
             * ------------------------End Sending Result-------------------
             */
            //new File(Preferences.SAVEPATH + File.separator + inputFile).delete();
            in.close();
            Tool.WriteOutput(strBuf.toString());
        } catch (FileNotFoundException ex) {
            logger.debug(ex.toString());
            System.out.println(ex.toString());
        } catch (IOException ex) {
            logger.debug(ex.toString());
            System.out.println(ex.toString());
        } 
        

    }

    private void downloadinput() {
        
        /*
         * ------------------------download DATA-------------------
         */  
            StringTokenizer filetoken = new StringTokenizer(inputPath, "/");
            while (filetoken.hasMoreTokens()) {
                inputFile = filetoken.nextToken();
            }
            File filein = new File(Preferences.SAVEPATH + File.separator + inputFile);
            
            if(!filein.exists()) {
            
                strBuf.append(String.format("DownloadFile\t%s\n",inputFile));
                Date date = new Date();
                strBuf.append(String.format("DownloadStart\t%s\n",dateFormat.format(date).toString()));
                FtpDownload download = new FtpDownload(inputPath, inputFile);
                ftpthread.execute(download);
                ftpthread.shutdown();
                while (!ftpthread.isTerminated()) {}

            }
            
        
    }
    
    
    
      synchronized static void MergeHashMap(HashMap hash) {

        for (Iterator<String> iterator = hash.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            if (partHashMap.containsKey(key)) {
                partHashMap.put(key, (Integer) partHashMap.get(key) + (Integer) hash.get(key));
            } else {
                partHashMap.put(key, (Integer) hash.get(key));
            }
        }
//        if (partHashMap.containsKey("traversing")) {
//            Integer one = (Integer) partHashMap.get("traversing");
//            System.out.println("Value mapped with key \"traversing\" is " + one);
//        }
    }

    synchronized static void SendMyMap() {
        ObjectOutputStream outputStream = null;
        try {

            InetAddress saddr = InetAddress.getByName(serverCon.getIPAddress().toString());
            SocketAddress socketAddress = new InetSocketAddress(saddr, 6347);
            Socket socket = new Socket();
            socket.connect(socketAddress, 30000);
            socket.setKeepAlive(true);
            outputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            outputStream.writeObject(partHashMap);
            outputStream.flush();
            outputStream.close();
            socket.close();
            System.out.println("Sent my HashMap");
            Tool.WriteOutput("Finish MapReduce Job\n");
            partHashMap.clear();
        } catch (IOException ex) {
            logger.debug(ex.toString());
            System.out.println(ex.toString());
        }
    }
}
