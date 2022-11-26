
/**
 * This code is from
 * @author Mateusz Jarzyna
 * Read the full article https://dev.to/mateuszjarzyna/build-your-own-http-server-in-java-in-less-than-one-hour-only-get-method-2k02
 *
 * GAT -- I have made this multithreaded
 * Now it is the complete driver program for all modes
 */
import java.io.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
//import java.net.SocketAddress;
import java.net.InetSocketAddress;

//import java.nio.file.Files;
//mport java.nio.file.Paths;
//import java.nio.file.Path;

public class NjavaX {
    public static ServerSocket server;

    public static void main(String[] args) throws Exception {
        

        /*
         * Program may run in one of three modes....
         * 0 -- program is regular web server
         * 1 -- program is proxy server
         *      It listens on 8080 and writes to counterpart on server in a socket number
         *      specifed in the config file. This may use load balancing.
         * 2 -- It is the counter part running on the final server
         * NOTE (+) is to be added soon
         * 3(+)-- It is a local proxy -- set system settings to use this as a proxy
         * 4(+)-- We can call this a direct proxy, that is a proxy for specific websites
         *        It is implemented by connecting to specific port associated with a URL
         * NOTE: The config file will provide the mode and if in mode 1 a list of
         * IP address: port numbers
         * if in mode 2 it just provides a port number to listen
         * 
         */
        

        GlobalInfo infoHolder = GlobalInfo.getInstance();

        String wDrive = "";
        String os = System.getProperty("os.name");
        System.out.println(" OS: " + os);
        if (os.contains("Windows")) {
            infoHolder.setWdrive("C:");
        }
    
       
        //
        // Read config file here FILEPATH: "./NjavaX.conf"
        // Set Variables from Config File using ConfigReader
        ConfigReader myConfigReader = new ConfigReader("./NjavaX.conf");

        // set mode
        List<String>temp = myConfigReader.get("mode"); // temp is reused for each of the variables
        if (!(temp.get(0).equals("default"))) {
            infoHolder.setMode(Integer.parseInt(temp.get(0)));
        }

        // set portNo
        temp = myConfigReader.get("portNo");
        if (!(temp.get(0).equals("default"))) {
            infoHolder.setPortNo(Integer.parseInt(temp.get(0)));
        }

        // set landingPath
        temp = myConfigReader.get("landingPath");
        if (!(temp.get(0).equals("default"))) {
            infoHolder.setLandingPath(temp.get(0));
        }

        // set wDrive
        temp = myConfigReader.get("wDrive");
        if(!(temp.get(0).equals("default"))) {
            infoHolder.setWdrive(temp.get(0));
        }

        // set entryServers
        temp = myConfigReader.get("entryList");
        if (!(temp.get(0).equals("default"))) {
            for (Integer i=0; i<temp.size()/2; i++) {
                String[] firstArg = temp.get(2*i).split(":",2);
                String[] secondArg = temp.get(2*i+1).split(":",2);

                String tempHost = "";
                Integer tempPort = -1;
                if (firstArg[0].equalsIgnoreCase("hostName") && secondArg[0].equalsIgnoreCase("portNumber")) {
                    tempHost = firstArg[1];
                    tempPort = Integer.parseInt(secondArg[1]);
                }
                else if (firstArg[0].equalsIgnoreCase("portNumber") && secondArg[0].equalsIgnoreCase("hostName")) {
                    tempPort = Integer.parseInt(firstArg[1]);
                    tempHost = secondArg[1];
                }
                else {
                    // WARNING
                    System.out.println("WARNING: Must have a hostName and portNumber for each ServerEntry. Server was NOT added!");
                }
                if (tempPort != -1) {
                    infoHolder.addServerEntry(tempHost, tempPort);
                }
            }
        }
        //
        // config file may be overridden with command line
        int dbmode = infoHolder.getMode();
        ParseCommandArgs(args);
        // now set new default mode 
        int mode = infoHolder.getMode();
        System.out.printf(" mode %d, dbmode %d\n",mode, dbmode);

        // if mode == 2, and port number has not been set -- set it to 8090;
        // if it has been set, or if mode is not 2, then just return the port number.
        
        int portno = infoHolder.ConditionalSetDefaultPort();
        
        System.out.println("*******************************************************************");
        System.out.println("* Parameters are set as follows:");
        System.out.printf("*  mode set to %d,\n",infoHolder.getMode());
        System.out.printf("*  port set to %d,\n",infoHolder.getPortno());
        System.out.printf("*  cofig file set to: %s,\n",infoHolder.getConfigPath());
        System.out.printf("*  landing folder set to %s,\n",infoHolder.getLandingPath());
        System.out.println("*******************************************************************\n");

        System.out.println();
        // DEBUG TEST of map in Global info
        /*infoHolder.setProxyEntry("googler", "144.0.0.1", 80);
        //List<Object> rvalue = infoHolder.getProxyEntry("googler");
        //String dbip = rvalue.get(0).toString();
        //int dbport = (int)rvalue.get(1);
        //System.out.printf(" DEbug: returned: %s , %d\n",dbip, dbport);
        */
        // END DEBUG
        
        if (mode == 1) {
            runReverseProxyServer();
        }
        if (mode == 3) {
            runProxyServer();
        }
        if (mode == 4) {
            runSpecificProxy();
        }
        // mode 0 or 2
        try {

            // server is listening on port 8080
            server = new ServerSocket(portno);
            // server.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
                if (mode == 0)
                    System.out.println("Web server before accept");
                else
                    System.out.printf("Proxy partner before accept on port no: %d\n", portno);
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server.  Trying to find the IP of the connected
                String clientIpAddress = ((InetSocketAddress) client.getRemoteSocketAddress()).getAddress().toString();
                System.out.printf("New client connected, remote addr: %s\n",clientIpAddress);
                //System.out.printf("New client connected, inet: %s\n",client.getInetAddress());
                //System.out.printf("New client connected: Host: %s\n",client.getInetAddress().getHostAddress());

                // create a new thread object
                ClientHandler clientSock = new ClientHandler(client, mode, wDrive);

                // This thread will handle the client
                // separately
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            System.out.println("Exception: ");
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
//************************************************************************************************* */
//  Mode 1
    private static void runReverseProxyServer() {

        GlobalInfo infoHolder = GlobalInfo.getInstance();
        int portNo = infoHolder.getPortno();
        String lastIPConnected = "";
        boolean isLoadBalance = false;
        String pIP = "localhost"; // IP address of partner (this program on server)
        int pPort = 8090;

        if (infoHolder.getNumServerEntries() > 1) {
            isLoadBalance = true;
        }
        if (infoHolder.getNumServerEntries() == 1) { // no servers entered at all
            // REMOVE THIS CODE AND REPLACE WITH GET FROM GLOBALINFO
            pIP = "localhost"; // IP address of partner (this program on server)
            pPort = 8090;
        }
        //String wDrive = infoHolder.getWdrive();
        try {            
            // server is listening on port 8080 or specified
            server = new ServerSocket(portNo);
            // server.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
                System.out.printf("Reverse proxy server before accept on port %d\n",portNo);
                Socket client = server.accept();

                // NOTE: we only care about the last connection if we are doing load balancing
                // Displaying that new client is connected
                // to server

                String connectedIp = client.getInetAddress().getHostAddress();
                if (!lastIPConnected.equalsIgnoreCase(connectedIp)) {   /// new connection
                    System.out.println("*+*+*+*+*+ new connection");
                    lastIPConnected = connectedIp;
                }
                System.out.printf("New client connected, inet: %s\n",client.getInetAddress());
                //System.out.printf("New client connected, Host: %s\n",client.getInetAddress().getHostAddress());

                // get IP address and port number of the server for which we are a proxy
                /*
                 * NOTE:
                 *      First select a server to send to...
                 *      May be round robin, or may be based on header info 
                 * 
                 */
                
                // create a new thread object
                ProxyDutiesHandler ProxySock = new ProxyDutiesHandler(client, pIP, pPort);

                // This thread will handle the client
                // separately
                new Thread(ProxySock).start();
            }
        } catch (IOException e) {
            System.out.println("Exception: ");
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
// ************************************************************************************************* */
// Mode 3
    private static void runProxyServer() 
    {
        GlobalInfo infoHolder = GlobalInfo.getInstance();
        int portNo = infoHolder.getPortno();
        String lastIPConnected = "";
        try {

            // server is listening on port 8080
            server = new ServerSocket(portNo);
            System.out.printf(" in runProxyserver, Port: %d",portNo);
            // server.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
               
                System.out.printf("Proxy server before accept on port no: %d\n", portNo);
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server.  Trying to find the IP of the connected
                String clientIpAddress = ((InetSocketAddress) client.getRemoteSocketAddress()).getAddress().toString();
                System.out.printf("New client connected, remote addr: %s\n",clientIpAddress);
                //System.out.printf("New client connected, inet: %s\n",client.getInetAddress());
                //System.out.printf("New client connected: Host: %s\n",client.getInetAddress().getHostAddress());

                // create a new thread object
                ProxyHandler proxySock = new ProxyHandler(client);

                // This thread will handle the client
                // separately
                new Thread(proxySock).start();
            }
        } catch (IOException e) {
            System.out.println("Exception: ");
            //e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // ************************************************************************************************* */
    // Mode 4
    private static void runSpecificProxy()
    {
        GlobalInfo infoHolder = GlobalInfo.getInstance();
        int portNo = infoHolder.getPortno();
        try {

            // server is listening on various ports
            server = new ServerSocket(portNo);
            // server.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
               
                System.out.printf("Proxy server (specific) before accept on port no: %d\n", portNo);
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server.  Trying to find the IP of the connected
                String clientIpAddress = ((InetSocketAddress) client.getRemoteSocketAddress()).getAddress().toString();
                System.out.printf("New client connected, remote addr: %s\n",clientIpAddress);

                ProxyHandler ProxySocket = new ProxyHandler(client);

                // This thread will handle the client
                // separately
                new Thread(ProxySocket).start();
            }
        } catch (IOException e) {
            System.out.println("Exception: ");
            //e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
//************************************************************************************************* */
    private static void ParseCommandArgs(String[] args)
    {
        // quick out if no command line args
        if (args.length < 2) {
            System.out.println(" no command line arguments to process");
        }

        GlobalInfo infoHolder = GlobalInfo.getInstance();


        int len = args.length -1; // zero based offset
        //System.out.printf(" length of args array is %d\n",len);
        int index = 0;
        int m = -1;
        int portno = 0;
        String configfile = "";
        String landing = "";
        while (index < len) {
            if ((args[index].equalsIgnoreCase("mode")) || (args[index].equalsIgnoreCase("-mode")))  {
                try {
                    m = Integer.parseInt(args[index+1]);
                    infoHolder.setMode(m);
                }
                catch (NumberFormatException e) {
                    System.out.printf(" error in mode number specification -- using default: %d\n",infoHolder.getMode());
                }
                infoHolder.setMode(m);
                index = index + 2;
            } else if ((args[index].equalsIgnoreCase("portno")) || (args[index].equalsIgnoreCase("-portno")))  {
                try {
                    portno = Integer.parseInt(args[index+1]);
                    infoHolder.setPortNo(portno);
                }
                catch (NumberFormatException e) {
                    System.out.printf(" error in port specification -- using default %d\n",infoHolder.getPortno());
                }
                index = index + 2;
            } else if ((args[index].equalsIgnoreCase("config")) || (args[index].equalsIgnoreCase("-config")))  {
                    configfile = args[index+1];
                    infoHolder.setConfigPath(configfile);
                    index = index + 2;
            } else if ((args[index].equalsIgnoreCase("landing")) || (args[index].equalsIgnoreCase("-landing")))  {
                landing = args[index+1];
                infoHolder.setLandingPath(landing);
                index = index + 2;
            } else {
                System.out.printf(" ERROR invalid command line arg:  %s,  -- not processing the rest\n",args[index]);
                index = index + len;
            }

        }
        
        return; 
         
    }
    
}