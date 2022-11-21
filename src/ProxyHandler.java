//package WebServer;

import java.io.*;
//import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProxyHandler implements Runnable {
	static int instanceCount = 0;
	private final Socket client;
	private Socket totarget = null;
	int mode = 0;

	// Constructor
	public ProxyHandler(Socket socket) 
	{
		this.client = socket;
		instanceCount = instanceCount + 1;
	}

	public void run()
	{
		PrintWriter out = null;
		BufferedReader in = null;

		final byte[] Request = new byte[4096];
		final byte[] Reply = new byte[4096];
		
		System.out.println(" Current Thread: "); // + currentThread().getName());
		System.out.printf("+++Instance count: %d\n",instanceCount);
		GlobalInfo infoHolder = GlobalInfo.getInstance();
		try {

			final InputStream InputStreamClient = client.getInputStream();
			final OutputStream OutputStreamClient = client.getOutputStream();

		
			// get the inputstream of client
			in = new BufferedReader(
					new InputStreamReader(
							client.getInputStream()));

			String line;
			
			StringBuilder requestBuilder = new StringBuilder();
			while (!(line = in.readLine()).isBlank()) {
				System.out.print("-->line input: ");
				System.out.println(line);
				//requestBuilder.append(line + "\r\n");
				//System.out.println(requestBuilder.toString());
			}
			System.out.println(" end of reading");
			String request = requestBuilder.toString();
			String[] requestsLines = request.split("\r\n");
			String[] requestLine = requestsLines[0].split(" ");
			//String method = requestLine[0];
			//String path = requestLine[1];
			//String version = requestLine[2];
			//String host = requestsLines[1].split(" ")[1];

			List<String> headers = new ArrayList<>();
			for (int h = 2; h < requestsLines.length; h++) {
				String header = requestsLines[h];
				headers.add(header);
			}

			//NOTE############ This should be url
			String partner_IP = "120.0.0.1";
			int partner_Port = 80;
			totarget = new Socket(partner_IP, partner_Port);
			final InputStream InputStreampartner = totarget.getInputStream();
			final OutputStream OutputStreampartner = totarget.getOutputStream();

			Thread New_Thread = new Thread() {

				/*------------------------------------------------------------------- */
				public void run() {
					int Bytes_Read;
					System.out.println(" Thread started ");
					try {                                    // try read write
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
			} // end of run
		}; // end of thread block
			
		// client-to-server request thread
		New_Thread.start();
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
	} catch (IOException e) {
				System.out.println(" **************>>>>>>>>Error with server socket <<<<<<<<<<<<<<<<<<<");
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

	private static void sendResponse(Socket client, String status, String contentType, byte[] content)
			throws IOException {
	    System.out.println(" > ****** SendResponse Called");
		OutputStream clientOutput = client.getOutputStream();
		clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
		clientOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
		clientOutput.write("\r\n".getBytes());
		clientOutput.write(content);
		clientOutput.write("\r\n\r\n".getBytes());
		clientOutput.flush();
		client.close();
	}

	
}
