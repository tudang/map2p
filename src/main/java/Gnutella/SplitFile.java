/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dhtu
 */
public class SplitFile {

    static final int Chunk_Size = 4 * 1024 * 1024;
    static int nChunks = 0;
    static String fname = "";
    public static String localDirectory = "";
    
    public void setFname(String fname) {
        SplitFile.fname = fname;
    }

    static ArrayList<String> split() {
        
           DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
	   //get current date time with Date()
	   Date date = new Date();
           localDirectory = Preferences.SHAREPATH+System.getProperty("file.separator")+ dateFormat.format(date);
           System.out.println(localDirectory);
	   boolean success = new File(localDirectory).mkdir();
           if(success) {
           System.out.println("Directory: " 
                + localDirectory + " created");
            }
           
        File ifile = new File(fname);
        FileInputStream fis;
        ArrayList<String> filechunknames = new ArrayList<String>();
        String newName;
        FileOutputStream fos;
        int fileSize = (int) ifile.length();
        int read , readLength = Chunk_Size;
        byte[] byteChunk;
        try {
            fis = new FileInputStream(ifile);
            
            while (fileSize > 0) {
                if (fileSize <= Chunk_Size) {
                    readLength = fileSize;
                }
                byteChunk = new byte[readLength];
                read = fis.read(byteChunk, 0, readLength);
                fileSize -= read;
                assert (read == byteChunk.length);
                nChunks++;
                newName = String.format("%s/%s.part%02d",localDirectory,ifile.getName(), nChunks );
                fos = new FileOutputStream(new File(newName));
                fos.write(byteChunk);
                fos.flush();
                fos.close();
                filechunknames.add(newName);
            }
            fis.close();
            nChunks = 0;
        }
        
        
        catch (IOException ex) {
            Logger.getLogger(SplitFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return filechunknames;
    }

    void mergeFile() {
        ArrayList<String> fnames = new ArrayList<String>();
        ArrayList<File> files = new ArrayList<File>();
        File ofile = new File("/home/dhtu/input/originalFile.txt");

        for (int i = nChunks - 1; i >= 0; i--) {
            String newName = String.format("%s.part%03d", SplitFile.fname, i);
            fnames.add(newName);
        }
        
        Collections.sort(fnames);
        
        
        for (String filename : fnames) {
            files.add(new File(filename));
        }
        FileOutputStream fos;
        FileInputStream fis;
        byte[] fileBytes;
        int bytesRead ;

        try {


            fos = new FileOutputStream(ofile, true);
            for (File file : files) {
                fis = new FileInputStream(file);
                fileBytes = new byte[(int) file.length()];
                bytesRead = fis.read(fileBytes, 0, (int) file.length());
                assert (bytesRead == fileBytes.length);
                assert (bytesRead == (int) file.length());
                fos.write(fileBytes);
                fos.flush();
                fileBytes = null;
                fis.close();
                fis = null;
            }
            fos.close();
            fos = null;

        } catch (IOException ex) {
            Logger.getLogger(SplitFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

}