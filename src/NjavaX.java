
/**
 * This code is from
 * @author Mateusz Jarzyna
 * Read the full article https://dev.to/mateuszjarzyna/build-your-own-http-server-in-java-in-less-than-one-hour-only-get-method-2k02
 *
 * GAT -- I have made this multithreaded
 */
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

//import java.nio.file.Files;
//mport java.nio.file.Paths;
//import java.nio.file.Path;

public class NjavaX {
    public static ServerSocket server;

    public static void main(String[] args) {
        

        /*
         * Program may run in one of three modes....
         * 0 -- program is regular web server
         * 1 -- program is proxy server
         * it listens on 8080 and writes to counterpart on server in a socket number
         * specifed in the config file
         * 2 -- It is the counter part running on the final server
         * 
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
    
        ParseCommandArgs(args);
        //
        // Read config file here
        //
        //
        // now set new default mode 
        int mode = infoHolder.getMode();

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

        if (mode == 1) {
            runProxyServer();
        }

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
                    System.out.println("WEb server before accept");
                else
                    System.out.printf("Proxy partner before accept on port no: %d\n", portno);
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server.  Trying to find the IP of the connected
                String clientIpAddress = ((InetSocketAddress) client.getRemoteSocketAddress()).getAddress().toString();
                System.out.printf("New client connected, remote addr: %s\n",clientIpAddress);
                
                System.out.printf("New client connected, inet: %s\n",client.getInetAddress());
                System.out.printf("New client connected: Host: %s\n",client.getInetAddress().getHostAddress());

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

    private static void runProxyServer() {

        GlobalInfo infoHolder = GlobalInfo.getInstance();
        int portNo = infoHolder.getPortno();
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
                System.out.printf("Proxy server before accept on port %d\n",portNo);
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server
                System.out.printf("New client connected, inet: %s\n",client.getInetAddress());
                System.out.printf("New client connected, Host: %s\n",client.getInetAddress().getHostAddress());

                // get IP address and port number of the server for which we are a procy
                /*
                 * NOTE:
                 *      First select a server to send to...
                 *      May be round robin, or may be based on header info 
                 * 
                 */
                String pIP = "localhost"; // IP address of partner (this program on server)
                int pPort = 8090;
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