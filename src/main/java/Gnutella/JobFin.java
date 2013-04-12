/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;

/**
 *
 * @author dhtu
 */
public class JobFin extends Packet{
    
    
    public JobFin()
    {
	super(Packet.JOBFIN, 0);
    }
    
    JobFin(byte[] rawdata) {
        super(rawdata);
    }
    
}
