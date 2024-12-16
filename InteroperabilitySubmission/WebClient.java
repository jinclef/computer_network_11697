import java.io.*;
import java.net.*;

public class WebClient {
  public static void main(String[] args) {
    String proxyHost = "localhost"; // Proxy Server의 호스트
    int proxyPort = 8888; // Proxy Server의 포트

    try (Socket socket = new Socket(proxyHost, proxyPort);
        OutputStream output = socket.getOutputStream();
        InputStream input = socket.getInputStream()) {

      PrintWriter writer = new PrintWriter(output, true);
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));

      // 요청 전송
      writer.println("GET /studentID.html HTTP/1.1");
      writer.println("Host: " + proxyHost);
      writer.println();

      // 응답 출력
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

    } catch (IOException e) {
      System.err.println("WebClient error: " + e.getMessage());
    }
  }

}
