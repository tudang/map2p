/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dhtu
 */
public class WriteResultToFile implements Runnable {

    private ConcurrentHashMap<String, Integer> finalHashMap;
    private static org.apache.log4j.Logger logger;
    

    public WriteResultToFile(ConcurrentHashMap<String, Integer> hm) {
        finalHashMap = hm;
    }

    @Override
    public void run() {
        try {
            while (!Master.receivedresult.isEmpty()) {
                continue;
            }

            ObjectOutputStream oos = null;
           

            oos = new ObjectOutputStream(new FileOutputStream("result"));
            oos.writeObject(finalHashMap);
            oos.flush();
            oos.close();
            System.out.println("Finish Writing to Output.txt file");
            oos.close();
            finalHashMap.clear();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            Tool.WriteOutput(String.format("MRFinish\t%s\n", dateFormat.format(date).toString()));
            oos.close();
        } catch (IOException ex) {
            logger.debug(ex.toString());
        }

    }
}
