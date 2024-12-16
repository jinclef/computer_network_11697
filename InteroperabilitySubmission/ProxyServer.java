import java.io.*;
import java.net.*;

public class ProxyServer {
  public static void main(String[] args) {
    int proxyPort = 8888; // Proxy Server 포트
    String webServerHost = "localhost"; // Web Server 호스트
    int webServerPort = 8080; // Web Server 포트

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

      while (true) { // 지속적으로 요청 처리
        String line = clientReader.readLine();
        if (line == null || line.isEmpty()) {
          break; // 요청이 없으면 종료
        }

        System.out.println("Client Request: " + line);

        // WebServer와 새로운 연결 설정
        try (Socket webServerSocket = new Socket(webServerHost, webServerPort);
            InputStream webServerInput = webServerSocket.getInputStream();
            OutputStream webServerOutput = webServerSocket.getOutputStream()) {

          PrintWriter serverWriter = new PrintWriter(webServerOutput, true);
          BufferedReader serverReader = new BufferedReader(new InputStreamReader(webServerInput));

          // 클라이언트 요청 전달
          serverWriter.println(line);
          serverWriter.flush();

          // WebServer 응답 전달
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
