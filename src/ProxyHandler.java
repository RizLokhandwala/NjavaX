//package WebServer;

import java.io.*;
//import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.io.IOException;
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
	//private final Socket totarget = null;


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
		PrintWriter outtotarget = null;

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
			// read all the header stuff then throw it away

			while (!(line = in.readLine()).isBlank()) {
				System.out.print("-->line input: ");
				System.out.println(line);
				//requestBuilder.append(line + "\r\n");
				//System.out.println(requestBuilder.toString());
			}
			System.out.println(" end of reading");
			
			sendResponse(client);
			System.out.println(" after sendResponse");

			//NOTE############ This should be url
			//String surl = "http://www.tutorialspoint.com/";
			//String surl = "https://www.google.com/";
			String surl = "http://www.yahoo.com/";
      		if(isUrlValid(surl))
				System.out.printf(" valid url is %s",surl);
			/*surl = "www.tutorialspoint.com/";
			if(isUrlValid(surl))
				  System.out.printf(" valid url is %s",surl);
			surl = "www.codejava.net";
			if(isUrlValid(surl))
						System.out.printf(" valid url is %s",surl); 
			*/
			URL url = new URL(surl);
			//Socket s = new Socket(surl,80); 

			//String partner_IP = "120.0.0.1";
			//int partner_Port = 80;
			//totarget = new Socket(partner_IP, partner_Port);
			//final InputStream InputStreampartner = totarget.getInputStream();
			//final OutputStream OutputStreampartner = totarget.getOutputStream();


			// Get actual IP associated with this URL through DNS
			//InetAddress address = InetAddress.getByName(surl);
			//System.out.printf(" InetAddress is %s\n",address);


			HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
        	connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setAllowUserInteraction(true);
        	connection.connect();

			System.out.println(" after urlconnection connect");
			
			//final InputStream InputStreamptarget = connection.getInputStream();
        	//PrintWriter writer = new PrintWriter(connection.getOutputStream());
			final OutputStream OutputStreamtarget = connection.getOutputStream();
			final InputStream InputStreamTarget = connection.getInputStream();
			
			//final InputStream InputStreampartner = totarget.getInputStream();
			//final OutputStream OutputStreampartner = totarget.getOutputStream();


			System.out.println(" before new thread ");

			Thread New_Thread = new Thread() {

				/*------------------------------------------------------------------- */
				// read from client - write to server
				public void run() {
					int Bytes_Read;
					try {
						connection.connect();
					} catch (IOException e) {
						System.out.println(" (re) connect failed");

					}
					System.out.println(" Thread started ");
					try {                                    // try read write
						while ((Bytes_Read = InputStreamClient.read(Request)) != -1) {
							System.out.printf(" Request Bytes read: %d\n", Bytes_Read);
							OutputStreamtarget.write(Request, 0, Bytes_Read);
							OutputStreamtarget.flush();
							System.out.println(" aftrer write to target");
						}

					} catch (IOException e) {
						System.out.println(" exception after while read & write...");
						System.out.println(e.getMessage());
						// e.printStackTrace();
					}

					// Close the connections
					//try {
					//	OutputStreamtarget.close();
					//} catch (IOException e) {
					//}
			} // end of run
		}; // end of thread block
			
		// read from target write to client
		New_Thread.start();
			System.out.println(" STarting new thread");
			try {
				connection.connect();
			} catch (IOException e) {
				System.out.println(" (re) connect failed");
			}
			try { // try read write
				while ((Bytes_Read = InputStreamTarget.read(Reply)) != -1) {
					System.out.printf(" Reply Bytes read: %d\n", Bytes_Read);
				
					OutputStreamClient.write(Reply, 0, Bytes_Read);
					OutputStreamClient.flush();
					System.out.println(" aftrer write to client");
				}
			} catch (IOException e) {
				System.out.println(" *********::: Error with reading inputstreamtarget");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			// Close the connection
			OutputStreamClient.close();
	} catch (IOException e) {
				System.out.println(" **************>>>>>>>>Error with target socket <<<<<<<<<<<<<<<<<<<");
				System.out.println(e.toString());
		} finally {
			try {
				System.out.println(" ++++++++++closing up shop");
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
	private static boolean isUrlValid(String url) {
		try {
		   URL obj = new URL(url);
		   obj.toURI();
		   return true;
		} catch (MalformedURLException e) {
		   return false;
		} catch (URISyntaxException e) {
		   return false;
		}
	 }
	private static void sendResponse(Socket client) throws IOException 
	{
	    System.out.println(" > ****** SendResponse Called");
		OutputStream clientOutput = client.getOutputStream();
		//BufferedWriter proxyToClientBw;
		//proxyToClientBw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		//String line = ("HTTP/1.0 200 Connection established\r\n" +
		//			"ContentType: HTTP/Text\r\n" +
		//			"\r\n");

		clientOutput.write(("HTTP/1.0 200 Connection established\r\n" +
					"ContentType: HTTP/Text\r\n" +
					"\r\n").getBytes());
					
		//proxyToClientBw.write(line);
		clientOutput.write("\r\n\r\n".getBytes());
		clientOutput.flush();
		client.close();
	}     
	///clientOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
}
