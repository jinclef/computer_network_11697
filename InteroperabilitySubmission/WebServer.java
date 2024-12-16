import java.io.*;
import java.net.*;

public class WebServer {
  public static void main(String[] args) {
    int serverPort = 8080; // Web Server 포트
    System.out.println("Web Server is starting on port " + serverPort);

    try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Received connection from Proxy Server: " + clientSocket.getInetAddress());
        new Thread(() -> handleRequest(clientSocket)).start();
      }
    } catch (IOException e) {
      System.err.println("WebServer Error: " + e.getMessage());
    }
  }

  private static void handleRequest(Socket clientSocket) {
    try (InputStream input = clientSocket.getInputStream();
        OutputStream output = clientSocket.getOutputStream()) {

      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      PrintWriter writer = new PrintWriter(output, true);

      // 요청 파싱
      String line = reader.readLine();
      System.out.println("Request received: " + line);

      if (line != null && line.startsWith("GET")) {
        String requestedFile = line.split(" ")[1];
        System.out.println("Requested file: " + requestedFile);

        if (requestedFile.equals("/studentID.html")) {
          File file = new File("studentID.html");

          if (file.exists()) {
            System.out.println("File found: " + file.getAbsolutePath());
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html");
            writer.println("Connection: keep-alive"); // 지속 연결 헤더 추가
            writer.println();

            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
              String fileLine;
              while ((fileLine = fileReader.readLine()) != null) {
                writer.println(fileLine);
              }
            }
          } else {
            System.out.println("File not found: " + file.getAbsolutePath());
            writer.println("HTTP/1.1 404 Not Found");
            writer.println("Connection: close"); // 파일이 없으면 연결 종료
            writer.println();
            writer.println("<html><body><h1>404 Not Found</h1></body></html>");
          }
        } else {
          System.out.println("Invalid file request: " + requestedFile);
          writer.println("HTTP/1.1 404 Not Found");
          writer.println("Connection: close");
          writer.println();
          writer.println("<html><body><h1>404 Not Found</h1></body></html>");
        }
      }
    } catch (IOException e) {
      System.err.println("Error in WebServer handling: " + e.getMessage());
    } finally {
      try {
        clientSocket.close();
        System.out.println("Closed connection with Proxy Server.");
      } catch (IOException e) {
        System.err.println("Error closing Proxy Server socket: " + e.getMessage());
      }
    }
  }
}
