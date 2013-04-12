/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;
/**
 *
 * @author dhtu
 */
public class Join extends Packet {
    private int index = HEADER_LENGTH;
    public Join(int port, IPAddress ip, int GroupID) {
        super(JOIN, 8);
       
        

	// convert port to two bytes
	contents[index + 0] = (byte)(port >>> 8);
	contents[index + 1] = (byte)(port & 0xff);
	
	// convert ip address to 4 bytes; need to check format of ip
	// address -- Little Endian????
	contents[index + 2] = (byte)ip.getFirst();
	contents[index + 3] = (byte)ip.getSecond();
	contents[index + 4] = (byte)ip.getThird();
	contents[index + 5] = (byte)ip.getFourth();
        
        contents[index + 6] = (byte)(GroupID >>> 8);
	contents[index + 7] = (byte)(GroupID & 0xff);
    }
    
    public Join(byte[] rawdata)
    {
	super(rawdata);
    }
    
    public int getPort()
    {
	int port = (((contents[index + 0] & 0xff) << 8) | (contents[index + 1] & 0xff));
	return (port);
    }

    public IPAddress getIP()
    {
	return (new IPAddress((contents[index + 2] & 0xff), (contents[index + 3] & 0xff), (contents[index + 4] & 0xff), (contents[index + 5] & 0xff), getPort()));
    }
    
    public int getGroupID()
    {
	int GroupID = (((contents[index + 6] & 0xff) << 8) | (contents[index + 7] & 0xff));
	return (GroupID);
    }
}
