package Gnutella;


import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.connectors.DirectConnector;
import org.apache.log4j.Logger;

import java.io.File;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author dhtu
 */
public class FtpDownload implements Runnable {

    private FTPClient client;
    private String DownloadFile;
    private String localFile;
    public static String hostftp = Preferences.REPOSITORY;
    public static String userftp = "map2p";
    public static String passftp = "map2p";
    public static String folderftp = "map2p";
    
    private static Logger logger = Logger.getLogger(FtpDownload.class);
    
  
 

    FtpDownload(String inputPath, String inputFile) {
        DownloadFile = inputPath;

        DirectConnector dct = new DirectConnector();

        client = new FTPClient();
        client.setConnector(dct);
        localFile = inputFile;
    }

    @Override
    public void run() {
        Download();
    }
    
    private void Download() {
    System.out.printf("Downloading file %s\n", DownloadFile);
        try {
            client.connect(hostftp);
            client.login(userftp, passftp);
            client.changeDirectory(folderftp);
            client.setAutoNoopTimeout(10000);
            client.setPassive(true);
            client.noop();
            client.download(DownloadFile,new File(Preferences.SAVEPATH + File.separator + localFile), new MyTransferListener());
            CloseFTPconnection();
         } 
        catch (Exception ex) {
            System.out.printf("Download failed %s\n", DownloadFile);
            ex.printStackTrace();
             Redownload();
        }    
        finally {
           CloseFTPconnection();
        }
    }
    public void CloseFTPconnection() {
        try {
            if(client.isConnected())
                client.disconnect(true);
            }  catch (Exception ex) {
            System.out.printf("Download failed %s\n", DownloadFile);
            ex.printStackTrace();
             Redownload();
        }  
    }

    private void Redownload() {
        new FtpDownload(DownloadFile, localFile).run();
        
    }
    

}
