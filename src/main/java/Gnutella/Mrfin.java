/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;

/**
 *
 * @author dhtu
 */
public class Mrfin extends Packet{
    private int index = HEADER_LENGTH;
    
    
    Mrfin(byte[] rawdata) {
        super(rawdata);
    }
    
    Mrfin(int taskid, int task) {
        super(Packet.MRFIN, 4);
        
        contents[index + 0] = (byte)(taskid >>> 8);
	contents[index + 1] = (byte)(taskid & 0xff);
        
        contents[index + 2] = (byte)(task >>> 8);
	contents[index + 3] = (byte)(task & 0xff);
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
}
