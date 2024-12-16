import java.io.*;
import java.net.*;

public class WebServer {
  public static void main(String[] args) throws IOException {
    int port = 8080; // 웹 서버 포트 설정
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("Web Server running on port " + port);

    while (true) {
      try {
        Socket clientSocket = serverSocket.accept();
        handleRequest(clientSocket);
      } catch (IOException e) {
        System.out.println("Error accepting connection: " + e.getMessage());
      }
    }
  }

  private static void handleRequest(Socket clientSocket) {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      String requestLine = in.readLine();
      System.out.println("Request: " + requestLine);

      if (requestLine != null && requestLine.contains("GET /studentID.html")) {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><body><h1>Student ID: 2020017065</h1></body></html>");
      } else {

      }

      clientSocket.close();
    } catch (IOException e) {
      System.out.println("Error handling request: " + e.getMessage());
    }
  }
}
