/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;

import java.util.Arrays;

/**
 *
 * @author dhtu
 */
public class Mrinit extends Packet{
    private int index = HEADER_LENGTH;
    
    
    public Mrinit(int taskid, int task, String path, int path_length) {
        
        super(MRINIT,path_length+4);
        
        contents[index + 0] = (byte)(taskid >>> 8);
	contents[index + 1] = (byte)(taskid & 0xff);
        
        contents[index + 2] = (byte)(task >>> 8);
	contents[index + 3] = (byte)(task & 0xff);
        
        byte[] bytes = path.getBytes();
        int count = 3;
        for(byte i:bytes) {
            count++;
            contents[index+count] = i;
         }
       }

    Mrinit(byte[] rawdata) {
        super(rawdata);
    }
    
    public int getTaskID()
    {
	int TaskID = (((contents[index + 0] & 0xff) << 8) | (contents[index + 1] & 0xff));
	return (TaskID);
    }
    
    public int getTask()
    {
	int Task = (((contents[index + 2] & 0xff) << 8) | (contents[index + 3] & 0xff));
	return (Task);
    }
    
    public String getPath() {
        byte[] in_path = Arrays.copyOfRange(contents, index+4, index+(this.length()));
        String path = new String(in_path);
        return path;
        
         
    }
    
}
