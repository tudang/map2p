package Gnutella;

import java.io.*;
import java.util.StringTokenizer;

public class Preferences
{
  public static String  FILE_NAME = "preferences.txt";
  
  public static int MAX_LIVE = 20;
  public static int MAX_CACHE = 100;
  public static boolean AUTO_CONNECT = true;
  public static int PINGER_TIME = 60000;
  public static int CONNECTOR_TIME = 60000;
  public  static int numberThreads;
  public static String SHAREPATH ;
  public static String SAVEPATH ;
  public static String REPOSITORY;
  public static  String userftp = "a1228422";
  public static  String passftp = "mapreducep2p";
  public  static String FTP_Folder = "map2p";
  
      
  public static void readFromFile()
  {
    try
    {
          BufferedReader fileIn = new BufferedReader(new FileReader(FILE_NAME));
          String line;
          while ((line = fileIn.readLine()) != null)
          {
            if (line.startsWith("Host:"))
            {
              String address = line.substring(6);
              StringTokenizer t = new StringTokenizer(address, ":");
              Host h = new Host(t.nextToken(), Integer.parseInt(t.nextToken()));
              HostCache.addHost(h);
              continue;
            }
            else if (line.startsWith("Max-Live:"))
            {
              MAX_LIVE = Integer.parseInt(line.substring(10));
              continue;
            }
            else if (line.startsWith("Max-Cache:"))
            {
              MAX_CACHE = Integer.parseInt(line.substring(11));
              continue;
            }
            else if (line.startsWith("Auto-Connect:"))
            {
              AUTO_CONNECT = ((Boolean.valueOf(line.substring(14))).booleanValue());
              continue;
            }
            else if (line.startsWith("Pinger-Time:"))
            {
              PINGER_TIME = Integer.parseInt(line.substring(13));
              continue;
            }
            else if (line.startsWith("Connector-Time:"))
            {
              CONNECTOR_TIME = Integer.parseInt(line.substring(16));
              continue;
            }
            else if (line.startsWith("Shared-Directory:"))
            {
              SHAREPATH = line.substring(18);
              System.out.println("Shared-Directory is " + SHAREPATH);
              continue;
            }
            else if (line.startsWith("Download-Directory:"))
            {
              SAVEPATH = line.substring(20);
              System.out.println("Download-Directory is " + SAVEPATH);
              continue;
            }
            else if (line.startsWith("Repository:"))
            {
              REPOSITORY = line.substring(12);
              System.out.println("Repository is " + REPOSITORY);
              continue;
            }
            else if (line.startsWith("Number-Threads:"))
            {
              numberThreads = Integer.parseInt(line.substring(16));
              System.out.println("Number of workers is: " + numberThreads);
              continue;
            }
          }
          fileIn.close();
    }
        
    catch (IOException e)
    {
      System.out.println("Unable to read preferences file");
    }
  }
  
  
  public static void writeToFile()
  {
    try
    {
          PrintWriter fileOut = new PrintWriter(new FileWriter(FILE_NAME));
          for (int i = 0; i < HostCache.getCount(); i++)
          {
          fileOut.println("Host: " + HostCache.hosts.get(i).getName() + ":" + HostCache.hosts.get(i).getPort());
          }
          fileOut.println("Max-Live: " + MAX_LIVE);
          fileOut.println("Max-Cache: " + MAX_CACHE);
          fileOut.println("Auto-Connect: " + AUTO_CONNECT);
          fileOut.println("Pinger-Time: "+ PINGER_TIME);
          fileOut.println("Connector-Time: "+CONNECTOR_TIME);
          fileOut.println("Shared-Directory: " + SHAREPATH);
          fileOut.println("Download-Directory: " + SAVEPATH);
          fileOut.println("Repository: " + REPOSITORY);
          System.out.println("Preferences.txt saved");
          fileOut.close();
    }
    catch (IOException e)
    {
      System.out.println("Unable to write to preferences file");
    }
  }
}

    
      
