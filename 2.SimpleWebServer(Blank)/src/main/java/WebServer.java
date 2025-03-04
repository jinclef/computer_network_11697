import java.io.*;
import java.net.*;
import java.util.*;

/**
 * WebServer class implements a simple multi-threaded web server.
 * 
 * This class serves as the main entry point for the web server application.
 * Its primary responsibilities include:
 * 1. Initializing the server on a specified port
 * 2. Listening for incoming client connections
 * 3. Creating new threads to handle each client request
 * 
 * The server runs indefinitely, continuously accepting new connections
 * and spawning threads to process HTTP requests.
 */

public final class WebServer {
	public static void main(String argv[]) throws Exception {
		//// Check if command line argument for port number is provided
		if (argv.length < 1) {
			System.out.println("Usage: java WebServer <port number : >");
			return;
		}
		//
		// Get the port number from the command line.
		// int port = (new Integer(argv[0])).intValue();
		int port = 8888;

		// Mission 1(Handle Connection): create and bind a socket (Fill #1 ~ #2)
		// Fill #1 Create the Serversocket and wait for the TCP Connection
		// Establish the Serversocket wait for the TCP Connection
		ServerSocket socket = new ServerSocket(port);

		// Process HTTP service requests in an infinite loop.
		while (true) {
			System.out.println("wait for connection");
			Socket connection = socket.accept();
			HttpRequest request = new HttpRequest(connection);
			Thread thread = new Thread(request);
			thread.start();
		}

	}
}

final class HttpRequest implements Runnable {
	final static String CRLF = "\r\n";
	Socket socket;

	// Constructor
	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface.
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * ProcessRequest Method class handles HTTP Request Messages
	 * 1. Receive and send HTTP Request and HTTP Response
	 * 2. Parse HTTP request line and save
	 * 3. Parse HTTP Header line and save
	 * 
	 * The server runs indefinitely, continuously accepting new connections
	 * and spawning threads to process HTTP requests.
	 */
	private void processRequest() throws Exception {
		// Mission 2: parse the HTTP request (Fill #5 ~ #7)
		// Fill#5 Create input stream from socket to receive data from client
		// Fill#6 Create output stream via socket to send data to client
		// Fill#7, bufferedReader filter around the input stream to parse HTTP Request
		// to Get a reference to the socket's input and output streams.
		// https://docs.oracle.com/javase/8/docs/api/java/io/package-summary.html
		// Mission 2: Get input stream from the socket
		InputStream is = new DataInputStream(socket.getInputStream());
		// Mission 2: Get Output stream from the socket
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		// Set up input stream filters.
		// Mission 2: wrap InputStreamReader and BufferedReader filters around the input
		// stream
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		// Mission 2(2-A, 2-B, 2-C): parse the HTTP request (Fill #8 ~ #9)
		// Mission 2: Get the request line of the HTTP request message
		String requestLine = br.readLine();
		// Fill #9: Use StringTokenizer to HTTP request
		// StringTokenizer from Java.util
		// https://docs.oracle.com/javase/8/docs/api/java/util/package-summary.html
		StringTokenizer tokens = new StringTokenizer(requestLine);
		// Mission 2-A: Get method information, Optional Exercises
		String method = tokens.nextToken(); // Mission 2-A: Get Method information
		// Mission 2-B: Get URI information
		String fileName = tokens.nextToken();
		// Mission 2-C: Get HTTP Version information
		String version = tokens.nextToken();

		// Prepend a "." so that file request is within the current directory.
		fileName = "." + fileName;

		// Open the requested file.
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}
		// Debug info for private use
		System.out.println("Incoming!!!");
		System.out.println(requestLine);
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}
		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String contentLengthLine = null;
		String entityBody = null;

		/**
		 * Mission 3. Analyze the request and send an appropriate response
		 * Mission 3. If HTTP response message consisting of the requested file, make
		 * the code with 200 OK
		 * If the requested file is not present in the server, the server should send an
		 * HTTP “404 Not Found” message back to the client.
		 * If the request message is not proper, the server should send an HTTP “400 BAD
		 * REQUEST” message back to the client.
		 * and make more response codes for your HTTP web server
		 * Optional Projects. Not only for the Method “GET”, you also have to consider
		 * handling other Methods.
		 */
		if (fileExists) {
			// Fill#10. When requested file exists, Status Code 200 OK
			// Mission 3-A: Status Code 200 OK
			statusLine = "HTTP/1.1 200 OK" + CRLF;
			contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
			contentLengthLine = "Content-Length: " + getFileSizeBytes(fileName) + CRLF;

		} else {
			// #Fill#11. When requested file doesn’t exist, Status Code 404 NOT FOUND
			// Mission 3-B: Status Code 404 Not found
			statusLine = "HTTP/1.1 404 Not Found" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "<HTML>" +
					"<HEAD><TITLE>Not Found</TITLE></HEAD>" +
					"<BODY>Not Found</BODY></HTML>";
		}

		// Mission 3-C(option): Status Code 400 Bad Request
		if (!method.equals("GET")) {
			statusLine = "HTTP/1.1 400 Bad Request" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "<HTML>" +
					"<HEAD><TITLE>Bad Request</TITLE></HEAD>" +
					"<BODY>Bad Request</BODY></HTML>";
		}

		// Send the status line.
		os.writeBytes(statusLine);

		// Send the content type line.
		os.writeBytes(contentTypeLine);

		// Send the content length line.
		os.writeBytes(contentLengthLine);

		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);

		// Send the entity body.
		if (fileExists) {
			sendBytes(fis, os);
			fis.close();
		} else {
			// Mission 3: Send appropriate entity body
			os.writeBytes(entityBody);
		}

		// Close streams and socket.
		os.close();
		br.close();
		socket.close();
	}

	/**
	 * Method which sends the context
	 * 
	 * @param fis FileInputStream to transfer
	 * @param os  outputstream to client
	 */
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Copy requested file into the socket's output stream.
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

	/**
	 * Method to return appropriate
	 * 
	 * @param fileName
	 */
	private static String contentType(String fileName) {
		System.out.println("filename is " + fileName);
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		/**
		 * Mission 4, create an HTTP response message consisting of the requested file
		 * preceded by header lines
		 * Now, you are just handling text/html, is there any more context-types? Find
		 * and make codes for it.
		 */
		// #Fill 12 Detect appropriate file extensions and return appropriate response
		// type(audio)
		else if (fileName.endsWith(".mp3")) {
			return "audio/mpeg";
		} else if (fileName.endsWith(".wav")) {
			return "audio/wav";
		} else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		} else {
			return "application/octet-stream";
		}
	}

	/**
	 * Get the File name, and through the file name, get the size of the file.
	 * .@param fileName
	 */
	private static long getFileSizeBytes(String fileName) throws IOException {
		File file = new File(fileName);
		return file.length();
	}
	// This method returns the size of the specified file in bytes.
}
