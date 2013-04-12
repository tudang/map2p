/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;

import it.sauronsoftware.ftp4j.FTPClient;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JFileChooser;
import org.apache.log4j.Logger;

/**
 *
 * @author dhtu
 */
public class Master {

    static FTPClient client;
    static ArrayList<String> uploadFiles;
    static Stack receivedresult;
    static AtomicInteger index = new AtomicInteger(0);
    static ArrayList<Integer> assignTask = new ArrayList<Integer>();
    static ConcurrentHashMap<Connection, ArrayList<Integer>> assignTaskTable;
    static ConcurrentHashMap<String, Integer> finalHashMap;
    static Logger logger;
    static AtomicInteger sent = new AtomicInteger(0);

    static void FinHandler(Connection cliconn, Mrfin fin) {
        //System.out.println("Received result of task" + fin.getTaskID());
        int taskid = fin.getTaskID();
//                if (assignTask.contains(taskid)) {
//                    int pos = Master.assignTask.indexOf(taskid);
//                    assignTask.remove(pos);
//                    System.out.println("Remove task " + taskid);
//                }
        ArrayList<Integer> currentTasks = assignTaskTable.get(cliconn);
        if (currentTasks.contains(taskid)) {
            currentTasks.remove((Object) taskid);
            assignTaskTable.put(cliconn, currentTasks);
            if (index.get() < uploadFiles.size()) {
                AssignTask(cliconn);
            }
        }
        if (currentTasks.isEmpty()) {
            FinishTask(cliconn);
        }
//        if (index.get() < uploadFiles.size()) {
//            AssignTask(cliconn);
//        }
//        else if (assignTask.isEmpty() && sent.get() ==0)
//        {
//            FinishTask();
//            sent.set(1);
//        }
    }

    public Master() {
        MRGroup.getInstance();
    }

    public static void FormGroup() {

        logger = Logger.getLogger(Master.class);
        finalHashMap = new ConcurrentHashMap<String, Integer>(65536);
        assignTaskTable = new ConcurrentHashMap<Connection, ArrayList<Integer>>();
        Random randomnumber = new Random();
        int GroupID = randomnumber.nextInt(1000);
        MRGroup.reformgroup();
        MRGroup.setGroup(GroupID);
        Group groupmsg = new Group(GroupID);
        NetworkManager.writeToAll(groupmsg);


    }

    public static void Joinhandler(Connection con, Join join) {
        if (join.getGroupID() == MRGroup.getGroupID()) {
            MRGroup.addWorker(con);
        }
        Searcher.AddGroupMember(con);

    }

    //Split data into chunks;
    public static void splitData(String path) {
        SplitFile.fname = path;
        SplitFile.split();

    }

    //send data to Repository
    public static ArrayList<String> sendData(String localDirectory) {
        uploadFiles = new ArrayList();
        client = new FTPClient();
        File localdir = new File(localDirectory);
        ExecutorService threadExecutor = Executors.newFixedThreadPool(4);


        try {
            client.connect(FtpUpload.hostftp);
            client.login(FtpUpload.userftp, FtpUpload.passftp);
            client.changeDirectory(FtpUpload.folderftp);
            String filepath = client.currentDirectory();
            client.disconnect(true);
            for (File afile : localdir.listFiles()) {
                Runnable task = new FtpUpload(afile);
                threadExecutor.execute(task);
                uploadFiles.add(filepath + System.getProperty("file.separator") + afile.getName());
            }

            threadExecutor.shutdown();
            while (!threadExecutor.isTerminated()) {
            };

        } catch (Exception ex) {
            logger.debug(ex.toString());
        }
        return uploadFiles;
    }

    //Run MapReduceJob
    public static void RunMapReduce() {
        sent.set(0);
        index.set(0);
        receivedresult = new Stack();
        JFileChooser fc = new JFileChooser();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        int retval = fc.showOpenDialog(fc);
        if (retval == JFileChooser.APPROVE_OPTION) {


            File f = fc.getSelectedFile();
            String filepath = f.getAbsolutePath();
            Tool.WriteOutput(String.format("%d node(s) Process File\t%s\n", MRGroup.mrconnections.size(), filepath));
            Master.splitData(filepath);
            Date date = new Date();
            Tool.WriteOutput(String.format("Upload\t%s\n", dateFormat.format(date).toString()));
            uploadFiles = sendData(SplitFile.localDirectory);
            Tool.deleteDir(new File(SplitFile.localDirectory));
            Collections.sort(uploadFiles);

            Date date2 = new Date();
            Tool.WriteOutput(String.format("MRstart\t%s\n", dateFormat.format(date2).toString()));

//                Iterator<Connection> iter = MRGroup.mrconnections.iterator();
//                while (iter.hasNext() && index.intValue() < uploadFiles.size()) {
//                    Connection conn = iter.next();
//                    AssignTask(conn);
//                }
            int count = 0;
            while (index.intValue() < uploadFiles.size() && count < 3) {
                for (Connection conn : MRGroup.mrconnections) {
                    if (index.intValue() < uploadFiles.size()) {
                        AssignTask(conn);
                    }
                }

                count++;
            }

        }



    }

    public static void AssignTask(Connection conn) {

        Mrinit currentTask = new Mrinit(index.intValue(), 1, uploadFiles.get(index.intValue()),
                +uploadFiles.get(index.intValue()).length());
        NetworkManager.writeToOne(conn, currentTask);
        if (assignTaskTable.containsKey(conn)) {
            ArrayList<Integer> tasks = assignTaskTable.get(conn);
            tasks.add(index.intValue());
            assignTaskTable.put(conn, tasks);
        } else {
            ArrayList<Integer> tasks = new ArrayList<Integer>();
            tasks.add(index.intValue());
            assignTaskTable.put(conn, tasks);
        }
//        if (!assignTask.add(index.get())) {
//            System.err.println("Add assign task error");
//        }

        System.out.printf("Assign task %d file %s for %s\n", index.get(),
                uploadFiles.get(index.get()).toString(), conn.getIPAddress().toString());
        index.getAndIncrement();
    }

//    public static void FinishTask() {
//       JobFin currentTask = new JobFin();
//       for(Connection conn : MRGroup.mrconnections) {
//        NetworkManager.writeToOne(conn, currentTask);
//        System.out.println("Sent finish sign to "+ conn.getIPAddress().toString());
//        synchronized(receivedresult) {
//        receivedresult.push(conn.getIPAddress().toString());
//        System.out.println("Add sent: " + Master.receivedresult);
//            }
//        }
//       ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
//       WriteResultToFile waitresult = new WriteResultToFile(finalHashMap);
//       threadExecutor.execute(waitresult);
//       threadExecutor.shutdown();
//       
//     }
    public static void FinishTask(Connection c) {
        JobFin currentTask = new JobFin();
        NetworkManager.writeToOne(c, currentTask);
        System.out.println("Sent finish sign to " + c.getIPAddress().toString());
        synchronized (receivedresult) {
            receivedresult.push(c.getIPAddress().toString());
            System.out.println("Add sent: " + Master.receivedresult);
        }

        ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
        WriteResultToFile waitresult = new WriteResultToFile(finalHashMap);
        threadExecutor.execute(waitresult);
        threadExecutor.shutdown();

    }

    synchronized static void MergeHashMap(ConcurrentHashMap hash) {


        for (Iterator<String> iterator = hash.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            if (finalHashMap.containsKey(key)) {
                finalHashMap.put(key, (Integer) finalHashMap.get(key) + (Integer) hash.get(key));
            } else {
                finalHashMap.put(key, (Integer) hash.get(key));
            }
        }
//                if (finalHashMap.containsKey("traversing")) {
//                    Integer one = (Integer) finalHashMap.get("traversing");
//                    System.out.println("Value mapped with key \"traversing\" is " + one);
//                }
    }
}