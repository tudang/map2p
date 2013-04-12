package Gnutella;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author dhtu
 */
public class Tool {

    private static FileWriter fw;
    private static Logger logger;

    public synchronized static void WriteOutput(String format) {
        try {
            fw = new FileWriter("output.csv", true);
            fw.write(format);
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            logger.debug(ex.toString());
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }
}
