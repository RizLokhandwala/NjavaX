import java.io.*;
import java.util.*;

public class GlobalInfo {
    private static GlobalInfo single_instance = null;
    // NOTE:  Some of these are in command line arguments.  
    // Those with + are may be overridden with cofig file
    //  Those with ++ are MUST be provided by config file
    private int mode;               //+explained elsewhere -- see main
    private int portNo;             //+ This is the port to LISTEN on -- in NginX it is "Listen"
    private Boolean portSet;        // has the port been set or use default depending on the how the mode is set
    private String configPath;      // path to read configuration file -- this is a command line argument
    private String landingPath;     //++ path for landing file -- used in Mode=0, and Mode=2
    private String wDrive;          // for windows we can specify files not found on "C" drive.

    // tuple class -- these are entries of the server list for load balancing
    private class ServerEntry {
        public int flag;
        public String hostName;
        public int portNumber;
        public ServerEntry(String hn, int pn) {
            flag = 0;
            hostName = hn;
            portNumber = pn;
        }
    }
    //++ This is the list for load balancing
    List<ServerEntry> entryList = new ArrayList<ServerEntry> (); 
    // 
    // ==== Experimental Code
    Map<String,ServerEntry> proxyMap = new HashMap<>();
    void setProxyEntry(String name, String IP, int portno)
    {
        ServerEntry entry = new ServerEntry(IP , portno);
        proxyMap.put(name,entry);
    }
    List<Object> getProxyEntry(String name) 
    {
        ServerEntry entry = proxyMap.get(name);
        return Arrays.asList(entry.hostName, entry.portNumber);
    }
        
    // private contstructor -- set all program defaults here
    private GlobalInfo()
    {
        // set defaults here
        mode = 0;
        configPath = "./NjavaX.cnfg";
        landingPath = "/tmp/webpagefiles";
        wDrive = "";
        portSet = false;
        portNo = 8080;
    }
    // the sigleton
    public static GlobalInfo getInstance()
    {
        if (single_instance == null)
            single_instance = new GlobalInfo();

        return single_instance;
    }
    // getters and setters -- we cant contstruct with valid data
    int getMode() {return mode;};
    void setMode(int m) 
    {   
        if ((m < 0) || (m > 2)) {
            mode = 0;
            System.out.printf("Invalid Mode entered, set to %d\n",mode);
        } else {
            mode = m; 
        }
           
    };
    int getPortno() {return portNo;};
    void setPortNo(int p) {portNo = p;portSet = true;return;}
    String getConfigPath() {return configPath;}
    void setConfigPath(String path) {configPath = path;return;}
    String getLandingPath() {return landingPath;}
    void setLandingPath(String path) {landingPath = path;return;}
    String getWdrive() {return wDrive;}
    void setWdrive(String d) {wDrive = d;return;}

    // list of servers

    int getNumServerEntries() { return entryList.size(); }
    void addServerEntry(String hn, int pn)
    {
        ServerEntry entry = new ServerEntry(hn, pn);
        entryList.add(entry);
    }
    List<Object> getEntry(int index) {
        ServerEntry entry = entryList.get(index);
        return Arrays.asList(entry.hostName, entry.portNumber);
    }

    // if the port has not been set and the mode is one the default is 8090
    int ConditionalSetDefaultPort() 
    {
        if (portSet) {
            System.out.println(" port has been set already");
            return portNo;
        }
        if (mode == 2)
            portNo = 8090;
        return portNo;
    }

}