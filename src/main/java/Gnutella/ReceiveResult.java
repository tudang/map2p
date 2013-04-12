/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dangtu
 */
class ReceiveResult implements Runnable {

    static ConcurrentHashMap<String, Integer> partHashMap;
    private Socket ss;

    public ReceiveResult(Socket serversock) {
        ss = serversock;
    }

    @Override
    public void run() {
        try {
            BufferedInputStream bis = new BufferedInputStream(ss.getInputStream());
           // do {
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            partHashMap = (ConcurrentHashMap) obj;
           // } while(bis.available() > 0);
            
            bis.close();
            
            Master.MergeHashMap(partHashMap);
            synchronized(Master.receivedresult) {
          if(!Master.receivedresult.isEmpty()) 
            Master.receivedresult.pop();
            System.out.println("After remove" + Master.receivedresult);
        }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ReceiveResult.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReceiveResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}