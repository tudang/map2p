package Gnutella;



import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * The methods in this class are called when certain events at up- and 
 * downloads are triggered, like transfer started, etc...
 * @author Robert
 */
public class MyTransferListener implements FTPDataTransferListener {

    private int bytesTransferred = 0;
    Boolean testVar;

    /**
     * Outputs a message when a data transfer has started
     */
    public void started() {
        System.out.println("Data transfer started.");
    }

    /**
     * Triggered throughout the transfer, to show progress
     */
    public void transferred(int length) {
        bytesTransferred += length;
        int MB = 2* 1024 * 1024;
        if (bytesTransferred % MB == 0) {
            System.out.println(bytesTransferred / MB + "MB transferred.");
            testVar=true;
        }
    }

    /**
     * Outputs a message when a data transfer has been completed
     */
    public void completed() {
        System.out.println("Data transfer completed ("+bytesTransferred+" bytes transferred).");
    }

    /**
     * Outputs a message when a data transfer has been aborted
     */
    public void aborted() {
        System.out.println("Transfer completed.");
    }

    /**
     * Outputs a message when a data transfer has failed
     */
    public void failed() {
        System.out.println("Transfer failed.");
        
    }
}