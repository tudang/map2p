package Gnutella;

/**
 * Big hairy GUI
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class Searcher {
    // private static int Speed has to come from the user.
    
    static LinkedList searches = new LinkedList();
    static ConnectionPanel myconnectionpanel;

    static void updateGroupJoin(int groupID) {
        myconnectionpanel.updateGroup(groupID);
    }

    static void AddGroupMember(Connection worker) {
        myconnectionpanel.AddGroupMember(worker);
    }

    static void ResetGroupMember() {
       myconnectionpanel.ResetGroupMember();
    }

    //added constructor
    public Searcher() {
        TabbedPaneFrame frame = new TabbedPaneFrame();

        myconnectionpanel = frame.getConnectionPanel();

        frame.show();
    }

    public static void inform(IPAddress ip, QueryHit qh) {
        Integer port = new Integer(qh.getPort());
        String myip = qh.getIP().toString();

        Iterator iter = searches.iterator();
        while (iter.hasNext()) {
            Query b = (Query) iter.next();
            if (qh.compare(b)) {
                ResultSet r = qh.getResults();
                while (r.more()) {
                    Integer index = new Integer(r.getIndex());
                    Integer size = new Integer(r.getFilesize());
                    String name = r.getName();

                }
            }
        }
    }

    public static void updateInfo(int hosts, int totalkb, int totalfiles) {
        ConnectionPanel.updateStats(hosts, totalfiles, totalkb);
    }

    public static void updateAddedConnection(Connection c) {
        ConnectionPanel.addAConnection(c.getIPAddress().toString(), c.getIPAddress().getPort(), c.getTypeString(), "Connected");
    }

    public static void updateRemovedConnection(IPAddress ip) {
        ConnectionPanel.removeAConnection(ip.toString(), ip.getPort(), "Disconnected");
    }

    public static void updateHostCache(Host h, boolean flag) {
        if (flag) {
            ConnectionPanel.addAHostInCache(h.getName(), h.getPort());
        } else {
            ConnectionPanel.removeAHostInCache(h.getName(), h.getPort());
        }
    }
}
class TabbedPaneFrame extends JFrame implements ChangeListener {

    private JTabbedPane tabbedPane;
    private ConnectionPanel cPanel;
    private JMenuItem newMarmalade;
    private JMenuItem exitMarmalade;

    public TabbedPaneFrame() {
        setTitle("Marmalade");

        // get screen dimensions
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int inset = 100;
        setBounds(inset, inset, 800, 550);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        newMarmalade = fileMenu.add("New");
        exitMarmalade = fileMenu.add("Exit");

        MenuHandler m = new MenuHandler();
        newMarmalade.addActionListener(m);
        exitMarmalade.addActionListener(m);

        cPanel = new ConnectionPanel();


        Border line = BorderFactory.createLineBorder(Color.black);
        cPanel.setBorder(line);




        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(this);
        tabbedPane.addTab("Connections", cPanel);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Preferences.writeToFile();
                System.exit(0);
            }
        });

        getContentPane().add(tabbedPane, "Center");
    }

    /**
     * Inner class - MenuHandler
     */
    class MenuHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == newMarmalade) {
            } else if (evt.getSource() == exitMarmalade) {
                Preferences.writeToFile();
                System.exit(0);
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        JTabbedPane pane = (JTabbedPane) event.getSource();
    }

    public ConnectionPanel getConnectionPanel() {
        return (cPanel);
    }
}

class ConnectionPanel extends JPanel {

    private static DefaultTableModel liveModel;
    private static DefaultTableModel cacheModel;
    private static DefaultTableModel statsModel;
    private static JTable liveTable;
    private static JTable cacheTable;
    private static JTable statsTable;
    private static JTextField ipField;
    private static JLabel ingroup;
    JButton btnFormGroup;
    JButton btnMapReduce;
    JTable tbGroupMember;
    DefaultTableModel groupmodel;
    private static DefaultTableModel membergroupmodel;
    private static JTable tbGroup;

    public ConnectionPanel() {
        setLayout(null);
        ipField = new JTextField(20);
        add(ipField);
        ipField.setBounds(50, 200, 250, 25);

        JButton connectButton = new JButton("Connect");
        add(connectButton);
        connectButton.setBounds(320, 200, 150, 25);
        connectButton.addActionListener(new ConnectAction());

        liveModel = new DefaultTableModel(0, 0);
        liveModel.addColumn((Object) "Remote host");
        liveModel.addColumn((Object) "Port");
        liveModel.addColumn((Object) "Type");
        liveModel.addColumn((Object) "Status");

        liveTable = new JTable(liveModel);

        JScrollPane scroll = new JScrollPane(liveTable);
        scroll.setBackground(Color.blue);
        add(scroll);
        scroll.setBounds(50, 50, 700, 120);

        JButton deleteConnection = new JButton("Delete Connection");
        //add(deleteConnection);
        deleteConnection.setBounds(575, 200, 175, 25);
        deleteConnection.addActionListener(new DeleteConnectionAction());

        statsModel = new DefaultTableModel(0, 0);
        statsModel.addColumn((Object) "Hosts");
        statsModel.addColumn((Object) "Total Files");
        statsModel.addColumn((Object) "Total kB");

        statsTable = new JTable(statsModel);

        JScrollPane statsScroll = new JScrollPane(statsTable);
        statsScroll.setBackground(Color.blue);
        //add(statsScroll);
        statsScroll.setBounds(50, 280, 300, 50);

        Object[] newStatsRow = new Object[3];
        statsModel.addRow(newStatsRow);



        cacheModel = new DefaultTableModel(0, 0);
        cacheModel.addColumn((Object) "Remote host");
        cacheModel.addColumn((Object) "Port");

        cacheTable = new JTable(cacheModel);


        JScrollPane cacheScroll = new JScrollPane(cacheTable);
        cacheScroll.setBackground(Color.blue);
        add(cacheScroll);
        cacheScroll.setBounds(450, 280, 300, 150);

        JButton delete = new JButton("Delete host");
        add(delete);
        delete.setBounds(525, 440, 150, 25);
        delete.addActionListener(new DeleteAction());


        btnFormGroup = new JButton("Form Group");
        btnMapReduce = new JButton("MapReduce");
        ingroup = new JLabel("Is in group: ");
        groupmodel = new DefaultTableModel();
        groupmodel.addColumn("GroupID");
        tbGroup = new JTable(groupmodel);
        JScrollPane groupScroll = new JScrollPane(tbGroup);

        membergroupmodel = new DefaultTableModel();
        membergroupmodel.addColumn("Group Members");
        tbGroupMember = new JTable(membergroupmodel);
        JScrollPane membergroupScroll = new JScrollPane(tbGroupMember);



        add(btnFormGroup);
        btnFormGroup.setBounds(50, 280, 150, 25);
        btnFormGroup.addActionListener(new FormGroup());

        add(btnMapReduce);
        btnMapReduce.setBounds(50, 310, 150, 25);
        btnMapReduce.setEnabled(false);
        btnMapReduce.addActionListener(new RunMapReduce());

        add(ingroup);
        ingroup.setBounds(50, 340, 150, 25);

        add(groupScroll);
        groupScroll.setBounds(210, 280, 70, 55);

        add(membergroupScroll);
        membergroupScroll.setBounds(300, 280, 130, 150);
    }

    public static void addAConnection(String host, int port, String type, String status) {
        Object[] newRow = new Object[4];
        newRow[0] = host;
        newRow[1] = new Integer(port);
        newRow[2] = type;
        newRow[3] = status;
        liveModel.insertRow(0, newRow);
    }

    public static void removeAConnection(String host, int port, String status) {
        for (int i = 0; i < liveModel.getRowCount(); i++) {
            String ip = (String) (liveModel.getValueAt(i, 0));
            int tport = Integer.parseInt(liveModel.getValueAt(i, 1).toString());
            if (ip.equals(host) && (tport==port) ) {
                liveModel.removeRow(i);
            }
        }
    }

    public static void addAHostInCache(String host, int port) {
        Object[] newRow = new Object[2];
        newRow[0] = host;
        newRow[1] = new Integer(port);
        cacheModel.insertRow(0, newRow);
    }

    public static void removeAHostInCache(String host, int port) {
        for (int i = 0; i < cacheModel.getRowCount(); i++) {
            String h = (String) (cacheModel.getValueAt(i, 0));
            if (host.equals(h)) {
                cacheModel.removeRow(i);
            }
        }
    }

    public static void updateStats(int hosts, int files, int kb) {
        statsModel.setValueAt(new Integer(hosts), 0, 0);
        statsModel.setValueAt(new Integer(files), 0, 1);
        statsModel.setValueAt(new Integer(kb), 0, 2);
    }

    public static void updateGroup(int groupid) {
        ingroup.setText("Is in group: " + groupid);
    }

    public synchronized static void AddGroupMember(Connection con) {
        Object[]  a = new Object[1];
        a[0] = con.getIPAddress().toString();
        membergroupmodel.addRow(a);
    }

    void ResetGroupMember() {
        membergroupmodel.getDataVector().removeAllElements();
        }
    
    class ConnectAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            String ip = ipField.getText();
            StringTokenizer st = new StringTokenizer(ip, ":");
            if (st.countTokens() == 2) {
                ip = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                Connector connector = new Connector(ip, port);
                connector.run();
            }
        }
    }

    class DeleteAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            int rowIndex = cacheTable.getSelectedRow();
            String ip = (String) cacheTable.getValueAt(rowIndex, 0);
            Integer port = (Integer) cacheTable.getValueAt(rowIndex, 1);
            Host h = new Host(ip, port.intValue());
            HostCache.removeHost(h);

        }
    }

    class DeleteConnectionAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            int rowIndex = liveTable.getSelectedRow();
            String ip = (String) liveTable.getValueAt(rowIndex, 0);
            NetworkManager.notify(HostArray.getConnection(ip));


        }
    }

    class FormGroup implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            Object[] gid = new Object[1];
            Master.FormGroup();
            gid[0] = MRGroup.getGroupID();
            groupmodel.insertRow(0, gid);
            btnMapReduce.setEnabled(true);
//            btnFormGroup.setEnabled(false);
        }
    }

    class RunMapReduce implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
           
                Master.RunMapReduce();
           
           
            
        }
    }
}