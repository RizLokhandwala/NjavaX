//package WebServer;

import java.io.*;
//import java.net.ServerSocket;
import java.net.Socket;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;

// This actually handles the reverse proxy
public class ProxyDutiesHandler implements Runnable {
	private final Socket client;
	private Socket topartner;
	private String partner_IP;
	private int partner_Port;
	private static int instanceCount = 0;

	// Constructor
	public ProxyDutiesHandler(Socket socket, String pIP, int pPort) {
		this.client = socket;
		this.topartner = null;
		this.partner_IP = pIP;
		this.partner_Port = pPort;
		instanceCount = instanceCount + 1;
	}

	public void run() {
		PrintWriter outtoclient = null;
		BufferedReader in = null;
		//BufferedReader infrompartner = null;
		topartner = null;
		PrintWriter outtopartner = null;

		// InputStream InputStreamClient = null;
		// OutputStream OutputStreamClient = null;
		// InputStream InputStreampartner = null;
		// OutputStream OutputStreampartner = null;
		final byte[] Request = new byte[4096];
		final byte[] Reply = new byte[4096];

		try {

			System.out.println(
					String.format(" In ProxydutiesServer for connection to IP: %s Port: %d", partner_IP, partner_Port));

			topartner = new Socket(partner_IP, partner_Port);
			System.out.print(" topartner: ");
			System.out.println(topartner.toString());
			System.out.printf(" >+=+=+=+=+=> instance count: %d", instanceCount);

			final InputStream InputStreamClient = client.getInputStream();
			final OutputStream OutputStreamClient = client.getOutputStream();

			// final InputStream InputStreampartner = topartner.getInputStream();
			// final OutputStream OutputStreampartner = topartner.getOutputStream();

			final InputStream InputStreampartner = topartner.getInputStream();
			final OutputStream OutputStreampartner = topartner.getOutputStream();

			Thread New_Thread = new Thread() {

				/*------------------------------------------------------------------- */
				public void run() {
					int Bytes_Read;
					System.out.println(" Thread started ");
					try { // try read write
						while ((Bytes_Read = InputStreamClient.read(Request)) != -1) {
							System.out.printf(" Request Bytes read: %d\n", Bytes_Read);
							OutputStreampartner.write(Request, 0, Bytes_Read);
							OutputStreampartner.flush();
						}

					} catch (IOException e) {
						System.out.println(e.getMessage());
						// e.printStackTrace();
					}

					// Close the connections
					try {
						OutputStreampartner.close();
					} catch (IOException e) {
					}
				}
			};
			// client-to-server request thread
			New_Thread.start();
			/* END Thread */
			/*------------------------------------------------------------------- */
			int Bytes_Read;
			try { // try read write
				while ((Bytes_Read = InputStreampartner.read(Reply)) != -1) {
					System.out.printf(" Reply Bytes read: %d\n", Bytes_Read);
					OutputStreamClient.write(Reply, 0, Bytes_Read);
					OutputStreamClient.flush();
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
				// e.printStackTrace();
			}
			// Close the connection
			OutputStreamClient.close();

		} catch (Exception e) {
			System.out.println(" **************>>>>>>>>Error with server socket <<<<<<<<<<<<<<<<<<<");
			System.out.println(e.toString());
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
	/*
	 * private static void sendResponse(Socket partner, String status, String
	 * contentType, byte[] content)
	 * throws IOException {
	 * System.out.println(" > ****** SendResponse Called");
	 * OutputStream partnerOutput = partner.getOutputStream();
	 * partnerOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
	 * partnerOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
	 * partnerOutput.write("\r\n".getBytes());
	 * partnerOutput.write(content);
	 * partnerOutput.write("\r\n\r\n".getBytes());
	 * partnerOutput.flush();
	 * partner.close();
	 * }
	 */
}
