import java.io.*;
import java.net.*;

public class WebClient {
  public static void main(String[] args) {
    String serverAddress = "172.16.164.120";
    int serverPort = 8888;
    String request = "GET /studentID.html HTTP/1.1\r\nHost: " + serverAddress + "\r\n\r\n";

    try {
      Socket socket = new Socket(serverAddress, serverPort);
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      out.println(request);

      String responseLine;
      StringBuilder response = new StringBuilder();

      // 서버로부터 받은 응답을 모두 저장
      while ((responseLine = in.readLine()) != null) {
        response.append(responseLine).append("\n");
      }

      // HTML 태그를 제거하는 정규 표현식
      String htmlResponse = response.toString();
      String plainText = htmlResponse.replaceAll("<[^>]*>", ""); // HTML 태그 제거

      System.out.println("Response:\n" + plainText); // HTML 태그가 제거된 응답 출력

      socket.close();
    } catch (IOException e) {
      System.out.println("Error in Web Client: " + e.getMessage());
    }
  }
}
