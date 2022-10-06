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

public class NjavaX {
    public static ServerSocket server;
public static void main(String[] args) {
        try {

            // server is listening on port 8080
            server = new ServerSocket(8080);
            // server.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
                System.out.println("before accept");
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server
                System.out.println("New client connected"
                        + client.getInetAddress()
                                .getHostAddress());

                // create a new thread object
                ClientHandler clientSock = new ClientHandler(client);

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
  
  
  
}
