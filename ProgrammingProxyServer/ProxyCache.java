
/** 
* ProxyCache.java - Simple caching proxy 
* 
* $Id: ProxyCache.java,v 1.3 2004/02/16 15:22:00 kangasha Exp $ 
* 
*/
import java.net.*;
import java.io.*;
import java.util.*;

public class ProxyCache {
  /** Port for the proxy */
  private static int port;
  /** Socket for client connections */
  private static ServerSocket socket;

  /** Create the ProxyCache object and the socket */
  public static void init(int p) {
    port = p;
    try {
      socket = new ServerSocket(port);
    } catch (IOException e) {
      System.out.println("Error creating socket: " + e);
      System.exit(-1);
    }
  }

  public static void handle(Socket client) {
    Socket server = null;
    HttpRequest request = null;
    HttpResponse response = null;

    /*
     * Process request. If there are any exceptions, then simply
     * return and end this request. This unfortunately means the
     * client will hang for a while, until it timeouts.
     */

    /* Read request */
    try {
      BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream())); // fill in
      request = new HttpRequest(fromClient); // fill in
    } catch (IOException e) {
      System.out.println("Error reading request from client: " + e);
      return;
    }
    /* Send request to server */
    try {
      /* Open socket and write request to socket */
      server = new Socket(request.getHost(), request.getPort()); // fill in
      DataOutputStream toServer = new DataOutputStream(server.getOutputStream()); // fill in
      toServer.writeBytes(request.toString()); // fill in
    } catch (UnknownHostException e) {
      System.out.println("Unknown host: " + request.getHost());
      System.out.println(e);
      return;
    } catch (IOException e) {
      System.out.println("Error writing request to server: " + e);
      return;
    }
    /* Read response and forward it to client */
    try {
      DataInputStream fromServer = new DataInputStream(server.getInputStream()); // fill in
      response = new HttpResponse(fromServer); // fill in
      DataOutputStream toClient = new DataOutputStream(client.getOutputStream()); // fill in
      toClient.writeBytes(response.toString()); // fill in

      /* Write response to client. First headers, then body */
      client.getOutputStream().write(response.toString().getBytes(), 0, response.toString().length());
      client.getOutputStream().write(response.body, 0, response.body.length);

      client.close();
      server.close();
      /* Insert object into the cache */
      /* Fill in (optional exercise only) */
    } catch (IOException e) {
      System.out.println("Error writing response to client: " + e);
    }
  }

  /** Read command line arguments and start proxy */
  public static void main(String args[]) {
    int myPort = 0;

    try {
      myPort = Integer.parseInt(args[0]);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Need port number as argument");
      System.exit(-1);
    } catch (NumberFormatException e) {
      System.out.println("Please give port number as integer.");
      System.exit(-1);
    }

    init(myPort);

    /**
     * Main loop. Listen for incoming connections and spawn a new
     * thread for handling them
     */
    Socket client = null;

    while (true) {
      try {
        client = socket.accept();
        handle(client);
      } catch (IOException e) {
        System.out.println("Error reading request from client: " + e);
        /*
         * Definitely cannot continue processing this request,
         * so skip to next iteration of while loop.
         */
        continue;
      }
    }
  }
}