import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServer {
  public static void main(String[] args) {
    int proxyPort = 8888; // Proxy Server port
    String webServerHost = "localhost"; // Web Server host
    int webServerPort = 8080; // Web Server port

    System.out.println("Proxy Server is starting on port " + proxyPort);

    try (ServerSocket serverSocket = new ServerSocket(proxyPort)) {
      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("New connection from client: " + clientSocket.getInetAddress());
        new Thread(() -> handleClient(clientSocket, webServerHost, webServerPort)).start();
      }
    } catch (IOException e) {
      System.err.println("ProxyServer Error: " + e.getMessage());
    }
  }

  private static void handleClient(Socket clientSocket, String webServerHost, int webServerPort) {
    try (InputStream clientInput = clientSocket.getInputStream();
        OutputStream clientOutput = clientSocket.getOutputStream()) {

      BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientInput));
      PrintWriter clientWriter = new PrintWriter(clientOutput, true);

      while (true) { // connection keep alive
        String line = clientReader.readLine();
        if (line == null || line.isEmpty()) {
          break; // connection closed
        }

        System.out.println("Client Request: " + line);

        // new connection to WebServer
        try (Socket webServerSocket = new Socket(webServerHost, webServerPort);
            InputStream webServerInput = webServerSocket.getInputStream();
            OutputStream webServerOutput = webServerSocket.getOutputStream()) {

          PrintWriter serverWriter = new PrintWriter(webServerOutput, true);
          BufferedReader serverReader = new BufferedReader(new InputStreamReader(webServerInput));

          // pass client request to WebServer
          serverWriter.println(line);
          serverWriter.flush();

          // pass WebServer response to client
          String responseLine;
          while ((responseLine = serverReader.readLine()) != null) {
            clientWriter.println(responseLine);
            clientWriter.flush();
            System.out.println("Server Response: " + responseLine);
          }
        }
      }
    } catch (IOException e) {
      System.err.println("Error in ProxyServer handling: " + e.getMessage());
    } finally {
      try {
        clientSocket.close();
        System.out.println("Closed client connection.");
      } catch (IOException e) {
        System.err.println("Error closing client socket: " + e.getMessage());
      }
    }
  }
}