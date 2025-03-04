import java.io.*;
import java.net.*;

/**
 * WebClient class implements a simple web client.
 * 
 * Its primary responsibilities include:
 * 1. Initializing the tcp connection to web server
 * 2. send HTTP request and receive HTTP response
 */
public class WebClient {
    public static void main(String[] args) {
        // Set the host, port and resource to send HTTP Request
        String host = "172.16.160.146";
        int port = 8888;
        String resource = args[0];
        try (
                // Fill #1, Set TCP socket to HTTP Web Server
                Socket socket = new Socket(host, port);
                // Fill #2, create PrintWirter instance with socket’s OutputStream
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // Fill #3, Get input stream from server, and insert it to BufferedReader
                // instance
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

            /**
             * Improve your HTTP Client to provide other request Methods(POST, DELETE, …)
             * and also improve to handle headers(Content-Type, User-Agent, …)
             */
            // Mission 2: Send HTTP GET Request and Read and display the response
            // Fill#4, Send HTTP GET request
            out.println("GET " + resource + " HTTP/1.1");
            out.println("Host: " + host);
            out.println("Connection: close");
            out.println();

            // Mission 3: Read and display the response
            // Fill#5, Read and display the response
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
