package Gnutella;

import it.sauronsoftware.ftp4j.FTPClient;
import java.io.File;
import org.apache.log4j.Logger;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author dhtu
 */
public class FtpUpload implements Runnable {

    private FTPClient client;
    private File uploadFile;
    private static Logger logger = Logger.getLogger(Master.class);
    public static String hostftp = Preferences.REPOSITORY;
    public static String userftp = "map2p";
    public static String passftp = "map2p";
    public static String folderftp = "map2p";

    public FtpUpload(File afile) {
        uploadFile = afile;
        client = new FTPClient();
    }

    public FtpUpload(String path) {
        uploadFile = new File(path);
        client = new FTPClient();
    }

    @Override
    public void run() {
        try {
            client.connect(hostftp);
            client.login(userftp, passftp);
            client.changeDirectory(folderftp);
            client.upload(uploadFile, new MyTransferListener());
            client.disconnect(true);
        } catch (Exception ex) {
            logger.debug(ex.toString());
        }
    }
}
