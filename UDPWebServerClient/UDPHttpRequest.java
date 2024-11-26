import java.io.*;
import java.net.*;
import java.util.*;

final class UDPHttpRequest implements Runnable {
  final static String CRLF = "\r\n";
  // Mission 1. Fill in #4 Changed to DatagramSocket
  // Fill in #4 Added to store the received packet
  // /* Fill in */;
  DatagramSocket socket;
  DatagramPacket receivePacket;

  // Fill in #5 Constructor should be changed. Socket and packet information
  // should be transferred
  public UDPHttpRequest(DatagramSocket socket, DatagramPacket packet) throws Exception {
    // /* Fill in */;
    this.socket = socket;
    this.receivePacket = packet;
  }

  // Implement the run() method of the Runnable interface.
  public void run() {
    try {
      processRequest();
    } catch (Exception e) {
      System.out.println("Error: " + e);
      try {
        sendErrorResponse(500, "Internal Server Error", e.getMessage());
      } catch (IOException ioException) {
        System.out.println("Failed to send 500 error response: " + ioException.getMessage());
      }
    }
  }

  private void processRequest() throws Exception {
    // Mission 2. #Fill in #6 Get the request data from the packet
    // Received DatagramPacket  Byte  byteArrayInputStream  InputStream 
    // BufferedReader
    // /* Fill in */;
    byte[] data = receivePacket.getData();
    ByteArrayInputStream bais = new ByteArrayInputStream(data);
    BufferedReader br = new BufferedReader(new InputStreamReader(bais));

    // Get the request line of the HTTP request message.
    String requestLine = br.readLine();
    System.out.println("Request Line: " + requestLine);

    // Extract the filename from the request line.
    StringTokenizer tokens = new StringTokenizer(requestLine);
    String method = tokens.nextToken();
    String fileName = tokens.nextToken();
    String httpVersion = tokens.nextToken();

    // if the request is not a GET method, return 501 Not Implemented
    if (!method.equals("GET")) {
      sendErrorResponse(501, "Not Implemented", "Method " + method + " not Implemented");
      return;
    }

    // if the http version is not HTTP/1.0, return 505 HTTP Version Not Supported
    if (!httpVersion.equals("HTTP/1.0") && !httpVersion.equals("HTTP/1.1")) {
      sendErrorResponse(505, "HTTP Version Not Supported", "HTTP Version " + httpVersion + " not supported");
      return;
    }

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

    // Construct the response message.
    String statusLine = null;
    String contentTypeLine = null;
    String contentLengthLine = null;
    String entityBody = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    if (fileExists) {
      statusLine = "HTTP/1.0 200 OK" + CRLF;
      contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
      contentLengthLine = "Content-Length: " + getFileSizeBytes(fileName) + CRLF;
    } else {
      statusLine = "HTTP/1.0 404 Not Found" + CRLF;
      contentTypeLine = "Content-Type: text/html" + CRLF;
      entityBody = "<HTML><HEAD><TITLE>Not Found</TITLE></HEAD><BODY>File " + fileName + " Not Found</BODY></HTML>";
    }

    // Mission 3. Fill in #7 Create the header line as bytes with get Bytes
    // Fill in #7 create and write to ByteArrayOutputStream to send header line
    // /* Fill in */;
    baos.write(statusLine.getBytes());
    baos.write(contentTypeLine.getBytes());
    if (fileExists) {
      baos.write(contentLengthLine.getBytes());
    }
    baos.write(CRLF.getBytes());

    // transfer entity to ByteArrayOutputStream
    if (fileExists) {
      sendBytes(fis, baos);
      fis.close();
    } else {
      baos.write(entityBody.getBytes());
    }

    // Mission 3. Fill in #8 Create the byte to send stream(header and entity body)
    // Fill in #8 send Datagram with UDP Socket.
    // (ByteArrayOutputStream  ByteArray  DatagramPacket)
    // /* Fill in */;
    byte[] dataToSend = baos.toByteArray();
    DatagramPacket sendPacket = new DatagramPacket(dataToSend, dataToSend.length, receivePacket.getAddress(),
        receivePacket.getPort());
    socket.send(sendPacket);

    // Close streams and socket.
    br.close();
  }

  private void sendErrorResponse(int statusCode, String title, String message) throws IOException {
    String statusLine = "HTTP/1.0 " + statusCode + " " + title + CRLF;
    String contentTypeLine = "Content-Type: text/html" + CRLF;
    String entityBody = "<HTML><HEAD><TITLE>" + title + "</TITLE></HEAD><BODY>" + message + "</BODY></HTML>";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write(statusLine.getBytes());
    baos.write(contentTypeLine.getBytes());
    baos.write(CRLF.getBytes());
    baos.write(entityBody.getBytes());

    byte[] dataToSend = baos.toByteArray();
    DatagramPacket sendPacket = new DatagramPacket(dataToSend, dataToSend.length, receivePacket.getAddress(),
        receivePacket.getPort());
    socket.send(sendPacket);
  }

  /**
   * Method which sends the context
   * 
   * @param fis FileInputStream to transfer
   * @param os  outputstream to client
   */
  private static void sendBytes(FileInputStream fis,
      OutputStream os) throws Exception {
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
    if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
      return "text/html";
    }
    /**
     * create an HTTP response message consisting of the requested file preceded by
     * header lines
     * Now, you are just handling text/html, is there any more context-types? Find
     * and make codes for it.
     */
    if (fileName.endsWith(".ram") || fileName.endsWith(".ra")) {
      return "audio/x-pn-realaudio";
    }
    if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
      return "image/jpeg";
    }
    // custom
    if (fileName.endsWith(".mp4")) {
      return "video/mp4";
    }
    if (fileName.endsWith(".png")) {
      return "image/png";
    }
    if (fileName.endsWith(".txt")) {
      return "text/plain";
    }
    if (fileName.endsWith(".pdf")) {
      return "application/pdf";
    }
    if (fileName.endsWith(".doc")) {
      return "application/msword";
    }

    return "application/octet-stream";
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
