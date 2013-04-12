package Gnutella;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Downloader extends Thread
{
    private int myindex;
    private String filename;
    private String serverip;
    private int myport;
    private boolean oktodownload = false;
    private int filesize = 0;

    public Downloader (int index, String name, String ip, int port)
    {
	myindex = index;
	filename = name;
	serverip = ip;
	myport = port;
    }

    @Override
    public void run()
    {
	try
	    {System.out.println("Downloader started.");
		Socket s = new Socket(serverip, myport);
		Connection connection = new Connection(s, Connection.DOWNLOADING);
                
	

		String greetstring = ("GET /get/" + myindex + "/" + filename + " HTTP/1.0\r\nConnection: Keep-Alive\r\nRange: bytes=0-\r\n\r\n"); 
		byte[] greeting = greetstring.getBytes();

		connection.getByteWriter().write(greeting, 0, greeting.length);
		connection.getByteWriter().flush();
		
		String responseline;
                 
		while (!((responseline = connection.getTextReader().readLine()).equals(""))) // Run through the HTTP header
		    {  
			responseline = connection.getTextReader().readLine();
			if (responseline.startsWith("Content-length: "))
			    {
				filesize = Integer.parseInt(responseline.substring(16)); // Start reading right after the space
				oktodownload = true;
	
			    }
		    }

		if (oktodownload)
		    {
			File towrite = new File((SharedDirectory.getOurSavePath().getPath() + File.separatorChar + filename));
			if (towrite.createNewFile())
			    {
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(towrite));
	
				for (int i = 0; i < filesize; i++)
				    {
				    out.write((byte)connection.getByteReader().read());
				    if (((i % 10000) == 0) && (i != 0)) // Give the user an update every 10 kb of downloading.
                                    {}// Good Lord, Java's typecasting leads to absurd parentheses! -------------v
	
				    }
	
	
			    }
			else
			    {
	
			    }
		    }
		else
		    {
			
		    }
	    }
	catch (IOException e)
	    {
		System.out.println("Unable to connect.");
	    }
    }
}
		



