/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnutella;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dhtu
 */
public class MRGroup {
    private static int GroupID;
    public static List<Connection> mrconnections;
    private static MRGroup instance = new MRGroup();
    
    public static synchronized MRGroup getInstance() {
        return instance;
    }
    
private MRGroup() {
    mrconnections = new ArrayList<Connection>();
}
    
public static List<Connection> getWorker() {
    return mrconnections;
}


public static void addWorker(Connection host) {
        if(!mrconnections.contains(host))
            mrconnections.add(host);
    }
    
public static void reformgroup() {
    if(!mrconnections.isEmpty())
        mrconnections.clear();
    Searcher.ResetGroupMember();
}    
    public static int size() {
        return mrconnections.size();
    }

    public static void setGroup(int groupID) {
        GroupID = groupID;
    }
    
    public static int getGroupID() {
        return GroupID;
    }
    
}