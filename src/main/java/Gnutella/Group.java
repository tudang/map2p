/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;
/**
 *
 * @author dhtu
 */
public class Group extends Packet {
    private int index = HEADER_LENGTH;
    private IPAddress ip;
    
    public Group(int GroupID) {
        super(GROUP, 6);
       
        contents[index + 0] = (byte)(GroupID >>> 8);
	contents[index + 1] = (byte)(GroupID & 0xff);
        
        ip = null;
    }
    
    public Group(byte[] rawdata)
    {
	super(rawdata);
    }
    
 

    public IPAddress getIP()
    {
	return (ip);
    }
    
    public int getGroupID()
    {
	int GroupID = (((contents[index + 0] & 0xff) << 8) | (contents[index + 1] & 0xff));
	return (GroupID);
    }
    
    public void setIP(IPAddress ip)
    {
	this.ip = ip;
    }
}
