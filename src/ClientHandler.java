//package WebServer;

import java.io.*;
//import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
	private final Socket client;
	private String wDrive;
	int mode = 1;

	// Constructor
	public ClientHandler(Socket socket, int mde, String WDrive) {
		this.client = socket;
		this.wDrive = WDrive;
		this.mode = mde;
	}

	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		System.out.println(" Current Thread: "); // + currentThread().getName());
		try {

			// get the outputstream of client
			//out = new PrintWriter(
			//		client.getOutputStream(), true);

			// get the inputstream of client
			in = new BufferedReader(
					new InputStreamReader(
							client.getInputStream()));

			String line;
			if (mode == 2) {
				if ((line = in.readLine()) != null) {
					System.out.printf(" read handshake: %s\n", line);
				} else {
					System.out.println(" read of handshake failed");
				}
				OutputStream clientOutput = client.getOutputStream();
				clientOutput.write("\r\n\r\n".getBytes());
				clientOutput.flush();
			}
			StringBuilder requestBuilder = new StringBuilder();
			while (!(line = in.readLine()).isBlank()) {
				System.out.print("-->line input: ");
				System.out.println(line);
				requestBuilder.append(line + "\r\n");
				//System.out.println(requestBuilder.toString());
			}
			System.out.println(" end of reading");
			String request = requestBuilder.toString();
			String[] requestsLines = request.split("\r\n");
			String[] requestLine = requestsLines[0].split(" ");
			String method = requestLine[0];
			String path = requestLine[1];
			String version = requestLine[2];
			String host = requestsLines[1].split(" ")[1];

			List<String> headers = new ArrayList<>();
			for (int h = 2; h < requestsLines.length; h++) {
				String header = requestsLines[h];
				headers.add(header);
			}

			String accessLog = String.format("==>Client %s, method %s, path %s, version %s, host %s, headers %s",
					client.toString(), method, path, version, host, headers.toString());
			System.out.println(accessLog);

			Path filePath = getFilePath(path);
			System.out.print(" file path to check: ");
			System.out.println(filePath);
			if (Files.exists(filePath)) {
				// file exist
				String contentType = guessContentType(filePath);
				sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
			} else {
				// 404
				byte[] notFoundContent = "<h1>File Not found :(</h1>".getBytes();
				sendResponse(client, "404 Not Found", "text/html", notFoundContent);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
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

	/*
	 * Modify this for default directory to find files
	 */
	private Path getFilePath(String path) {
		if ("/".equals(path)) {
			path = "/index.html";
		}
		String directory = wDrive + "/tmp/webpagefiles";
		return Paths.get(directory, path);
	}

	private String guessContentType(Path filePath) throws IOException {
		System.out.print(" content type: ");
		System.out.println(Files.probeContentType(filePath));
		return Files.probeContentType(filePath);
	}
}
