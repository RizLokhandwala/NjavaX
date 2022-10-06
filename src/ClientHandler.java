//package WebServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
	private final Socket client;

	// Constructor
	public ClientHandler(Socket socket) {
		this.client = socket;
	}

	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		try {

			// get the outputstream of client
			out = new PrintWriter(
					client.getOutputStream(), true);

			// get the inputstream of client
			in = new BufferedReader(
					new InputStreamReader(
							client.getInputStream()));

			String line;
			StringBuilder requestBuilder = new StringBuilder();
			while (!(line = in.readLine()).isBlank()) {
				System.out.print("line input: ");
				System.out.println(line);
				requestBuilder.append(line + "\r\n");
				System.out.println(requestBuilder.toString());
			}
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
			/*
			 * while ((line = in.readLine()) != null) {
			 * 
			 * // writing the received message from
			 * // client
			 * System.out.printf(
			 * " Sent from the client: %s\n",
			 * line);
			 * out.println(line);
			 * }
			 */
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
	private static Path getFilePath(String path) {
		if ("/".equals(path)) {
			path = "/index.html";
		}

		return Paths.get("/tmp/webpagefiles", path);
	}

	private static String guessContentType(Path filePath) throws IOException {
		System.out.print(" content type: ");
		System.out.println(Files.probeContentType(filePath));
		return Files.probeContentType(filePath);
	}
}
