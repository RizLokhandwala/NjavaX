import java.io.*;
import java.util.*;

public class GlobalInfo {
    private static GlobalInfo single_instance = null;
    private int mode;
    private String configPath;
    // tuple class 
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

    // private contstructor
    private GlobalInfo()
    {
        // set defaults here
        mode = 0;
        configPath = "";
    }
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
           
           return;};
    String getConfigPath() {return configPath;}
    void setConfigPath(String path) {configPath = path;return;}
    int getNumServerEntries() {
        return entryList.size();
    }
    void addServerEntry(String hn, int pn)
    {
        ServerEntry entry = new ServerEntry(hn, pn);
        entryList.add(entry);
    }
    List<Object> getEntry(int index) {
        ServerEntry entry = entryList.get(index);
        return Arrays.asList(entry.hostName, entry.portNumber);
    }

}
