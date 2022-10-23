
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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

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
         */

        System.out.printf(" args: %s \n",args[0]);
        
        int mode = 0;
        // should set mode from GlobalInfo
        ParseCommandArgs(args);

        String wDrive = "";
        String os = System.getProperty("os.name");
        System.out.println(" OS: " + os);
        if (os.contains("Windows")) {
            wDrive = "C:";
        }
        // DEBUG
        /* 
        String name = wDrive + "/tmp/webpagefiles";
        System.out.println(" name is " + name);
        Path filePath = Paths.get(name, "index.html");
        if (Files.exists(filePath)) {
            System.out.println(" debug file exists");
        } else {
            System.out.println(" debug file www does not exist");
        }
        // END DEbug
        */
        System.out.println(String.format(" Mode: %d", mode));
        int portno = 8080;
        if (mode == 2) {
            portno = 8090; // This 8090 is for testing -- port number is actually in config file
        }
        if (mode == 1) {
            runProxyServer(portno, wDrive);
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
                // to server
                System.out.println("New client connected"
                        + client.getInetAddress()
                                .getHostAddress());

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

    private static void runProxyServer(int portno, String wDrive) {
        try {

            // server is listening on port 8080
            server = new ServerSocket(portno);
            // server.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
                System.out.printf("Proxy server before accept on port %d\n", portno);
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server
                System.out.println("New client connected"
                        + client.getInetAddress()
                                .getHostAddress());

                // get IP address and port number of the server for which we are a procy
                /*
                 * First select a server to send to
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
    private static int ParseCommandArgs(String[] args)
    {
        int len = args.length -1; // zero based offset
        System.out.printf(" length of args array is %d\n",len);
        int index = 0;
        int m = -1;
        String configfile = "";
        String landing = "";
        while (index < len) {
            if ((args[index].equalsIgnoreCase("mode")) || (args[index].equalsIgnoreCase("-mode")))  {
                try {
                    m = Integer.parseInt(args[index+1]);
                }
                catch (NumberFormatException e) {
                    m = 0;
                    System.out.printf(" error in mode number specification -- setting to %d\n",m);
                }
                index = index + 2;
            }
            else if ((args[index].equalsIgnoreCase("config")) || (args[index].equalsIgnoreCase("-config")))  {
                    configfile = args[index+1];
                    index = index + 2;
            } else if ((args[index].equalsIgnoreCase("landing")) || (args[index].equalsIgnoreCase("-landing")))  {
                landing = args[index+1];
                index = index + 2;
            } else {
                System.out.printf(" ERROR invalid command line arg:  %s,  -- not processing the rest\n",args[index]);
                index = index + len;
            }

        }
        System.out.printf(" mode set to %d\n",m);
        System.out.printf(" cofig file set to: %s\n",configfile);
        System.out.printf(" landing folder set to %s\n",landing);
        return m; // should return mode
         

    }

}