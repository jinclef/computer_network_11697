import java.io.*;
import java.net.*;

public class ProxyServer {
  public static void main(String[] args) {
    int proxyPort = 8888; // Proxy Server의 포트
    String webServerHost = "localhost"; // Web Server 호스트
    int webServerPort = 8080; // Web Server 포트

    System.out.println("Proxy Server is starting on port " + proxyPort);

    try (ServerSocket serverSocket = new ServerSocket(proxyPort)) {
      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("New connection from client: " + clientSocket.getInetAddress());
        new Thread(() -> handleClientRequest(clientSocket, webServerHost, webServerPort)).start();
      }
    } catch (IOException e) {
      System.err.println("ProxyServer Error: " + e.getMessage());
    }
  }

  private static void handleClientRequest(Socket clientSocket, String webServerHost, int webServerPort) {
    try (Socket webServerSocket = new Socket(webServerHost, webServerPort);
        InputStream clientInput = clientSocket.getInputStream();
        OutputStream clientOutput = clientSocket.getOutputStream();
        InputStream webServerInput = webServerSocket.getInputStream();
        OutputStream webServerOutput = webServerSocket.getOutputStream()) {

      // 클라이언트 요청을 WebServer로 전달
      BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientInput));
      PrintWriter serverWriter = new PrintWriter(webServerOutput, true);

      String line;
      while ((line = clientReader.readLine()) != null && !line.isEmpty()) {
        serverWriter.println(line); // WebServer로 요청 전송
        System.out.println("Client Request: " + line); // 요청 로그 출력
      }
      serverWriter.println(); // 빈 줄로 요청 끝 알림

      // WebServer 응답을 클라이언트로 전달
      BufferedReader serverReader = new BufferedReader(new InputStreamReader(webServerInput));
      PrintWriter clientWriter = new PrintWriter(clientOutput, true);

      while ((line = serverReader.readLine()) != null) {
        clientWriter.println(line); // 클라이언트로 응답 전송
        System.out.println("Server Response: " + line); // 응답 로그 출력
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
