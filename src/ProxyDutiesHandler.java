//package WebServer;

import java.io.*;
//import java.net.ServerSocket;
import java.net.Socket;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;

public class ProxyDutiesHandler implements Runnable {
	private final Socket client;
	private Socket topartner;
	private String partner_IP;
	private int partner_Port;

	// Constructor
	public ProxyDutiesHandler(Socket socket, String pIP, int pPort) {
		this.client = socket;
		this.topartner = null;
		this.partner_IP = pIP;
		this.partner_Port = pPort;
	}

	public void run() {
		PrintWriter outtoclient = null;
		BufferedReader in = null;
		BufferedReader infrompartner = null;
		topartner = null;
		PrintWriter outtopartner = null;

		InputStream InputStreamClient = null;
		OutputStream OutputStreamClient = null;
		InputStream InputStreampartner = null;
		OutputStream OutputStreampartner = null;
		final byte[] Request = new byte[4096];
		final byte[] Reply = new byte[4096];
		/*
		 * First select a server to send to
		 * 
		 */

		try {
			
			System.out.println(
					String.format(" In Proxyduties Server for connection to IP: %s Port: %d", partner_IP, partner_Port));

			topartner = new Socket(partner_IP, partner_Port);
			System.out.print(" topartner: ");
			System.out.print(topartner.toString());

			InputStreamClient = client.getInputStream();
			OutputStreamClient = client.getOutputStream();

			InputStreampartner = topartner.getInputStream();
			OutputStreampartner = topartner.getOutputStream();

		} catch (Exception e) {
			System.out.println(" **************>>>>>>>>Error with server socket <<<<<<<<<<<<<<<<<<<");
			System.out.println(e.toString());
		}

		int Bytes_Read;
		try {
			while ((Bytes_Read = InputStreamClient.read(Request)) != -1) {
				OutputStreampartner.write(Request, 0, Bytes_Read);
				OutputStreampartner.flush();
			}
			System.out.println(" -- end of reading from client, now write");

			while ((Bytes_Read = InputStreampartner.read(Reply)) != -1) {
				OutputStreamClient.write(Reply, 0, Bytes_Read);
				OutputStreamClient.flush();
			}

		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outtoclient != null) {
					outtoclient.close();
				}
				if (in != null) {
					in.close();
					client.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	/*private static void sendResponse(Socket partner, String status, String contentType, byte[] content)
			throws IOException {
	    System.out.println(" > ****** SendResponse Called");
		OutputStream partnerOutput = partner.getOutputStream();
		partnerOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
		partnerOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
		partnerOutput.write("\r\n".getBytes());
		partnerOutput.write(content);
		partnerOutput.write("\r\n\r\n".getBytes());
		partnerOutput.flush();
		partner.close();
	} */
}
