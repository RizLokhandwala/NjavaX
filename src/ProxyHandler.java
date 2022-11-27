//package WebServer;

import java.io.*;
//import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;  
//import java.net.HttpURLConnection;
import java.net.InetAddress;


//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;

public class ProxyHandler implements Runnable {
	static int instanceCount = 0;
	private final Socket client;
	private final Socket totarget = null;


	// Constructor
	public ProxyHandler(Socket socket, int port) 
	{
		this.client = socket;
		instanceCount = instanceCount + 1;
	}

	public void run()
	{
		int Bytes_Read;
		PrintWriter out = null;
		BufferedReader in = null;

		final byte[] Request = new byte[4096];
		final byte[] Reply = new byte[4096];
		
		System.out.println(" Proxy handler Current Thread: "); // + currentThread().getName());
		System.out.printf("+++Instance count: %d\n",instanceCount);
		GlobalInfo infoHolder = GlobalInfo.getInstance();
		try {
			System.out.println(
					String.format(" In specific Proxy Server for connection "));

			final InputStream InputStreamClient = client.getInputStream();
			final OutputStream OutputStreamClient = client.getOutputStream();

		
			// get the inputstream of client
			in = new BufferedReader(
					new InputStreamReader(
							client.getInputStream()));

			String line;
			
			while (!(line = in.readLine()).isBlank()) {
				System.out.print("-->line input: ");
				System.out.println(line);
				//requestBuilder.append(line + "\r\n");
				//System.out.println(requestBuilder.toString());
			}
			System.out.println(" end of reading");
			
			//String method = requestLine[0];
			//String path = requestLine[1];
			//String version = requestLine[2];
			//String host = requestsLines[1].split(" ")[1];


			//NOTE############ This should be url
			URL url = new URL("http://www.javatpoint.com/java-http-proxy-server");
			//String partner_IP = "120.0.0.1";
			//int partner_Port = 80;
			//totarget = new Socket(partner_IP, partner_Port);
			//final InputStream InputStreampartner = totarget.getInputStream();
			//final OutputStream OutputStreampartner = totarget.getOutputStream();


			// Get actual IP associated with this URL through DNS
			InetAddress address = InetAddress.getByName("www.javatpoint.com" );
			System.out.printf(" InetAddress is %s\n",address);


			URLConnection connection = url.openConnection();
        	connection.setDoOutput(true);
			connection.setAllowUserInteraction(true);
        	connection.connect();

			System.out.println(" after urlconnection connect");
			
			//final InputStream InputStreamptarget = connection.getInputStream();
        	//PrintWriter writer = new PrintWriter(connection.getOutputStream());
			final OutputStream OutputStreamtarget = connection.getOutputStream();
			final InputStream InputStreamTarget = connection.getInputStream();

			System.out.println(" before new thread ");

			Thread New_Thread = new Thread() {

				/*------------------------------------------------------------------- */
				public void run() {
					int Bytes_Read;
					System.out.println(" Thread started ");
					try {                                    // try read write
						while ((Bytes_Read = InputStreamClient.read(Request)) != -1) {
							System.out.printf(" Request Bytes read: %d\n", Bytes_Read);
							//OutputStreamtarget.write(Request, 0, Bytes_Read);
							//OutputStreamtarget.flush();
						}

					} catch (IOException e) {
						System.out.println(" after while read....");
						System.out.println(e.getMessage());
						// e.printStackTrace();
					}

					// Close the connections
					try {
						OutputStreamtarget.close();
					} catch (IOException e) {
					}
			} // end of run
		}; // end of thread block
			
		// client-to-server request thread
		New_Thread.start();
			System.out.println(" STarting new thread");
			try { // try read write
				while ((Bytes_Read = InputStreamTarget.read(Reply)) != -1) {
					System.out.printf(" Reply Bytes read: %d\n", Bytes_Read);
				
					connection = url.openConnection();
        			connection.setDoOutput(true);
					connection.setAllowUserInteraction(true);
        			connection.connect();
					OutputStreamClient.write(Reply, 0, Bytes_Read);
					OutputStreamClient.flush();
				}
			} catch (IOException e) {
				System.out.println(" *********::: Error with reading inputstreamtarget");
				System.out.println(e.getMessage());
				// e.printStackTrace();
			}
			// Close the connection
			OutputStreamClient.close();
	} catch (IOException e) {
				System.out.println(" **************>>>>>>>>Error with target socket <<<<<<<<<<<<<<<<<<<");
				System.out.println(e.toString());
		} finally {
			try {
				if (out != null) {
				out.close();
				}
				if (in != null) {
					in.close();
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
}
