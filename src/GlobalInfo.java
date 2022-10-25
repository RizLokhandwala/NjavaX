import java.io.*;
import java.util.*;

public class GlobalInfo {
    private static GlobalInfo single_instance = null;
    private int mode;
    private int portNo;
    private Boolean portSet;
    private String configPath;
    private String landingPath;
    private String wDrive;
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
    List<ServerEntry> entryList = new ArrayList<ServerEntry> (); 

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
        if (portSet)
            return portNo;;
        if (mode == 2)
            portNo = 8090;
        return portNo;
    }

}