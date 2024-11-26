import java.io.*;
import java.net.*;

/**
 * WebClient class implements a simple web client.
 * Its primary responsibilities include:
 * 1. Initializing the udp connection to web server
 * 2. send HTTP request and receive HTTP response
 */
public class UDPWebClient {
  public static void main(String[] args) {
    String host = "192.168.42.127";
    int port = 8888;
    String resource = args.length > 0 ? args[0] : "/NotExist.html"; // Take resource from args or use default
    resource = "/rsc" + resource; // Add /rsc to resource
    try {
      // Mission 1. Fill in #11 define InetAddress with host ip and initial
      // DatagramSocket
      InetAddress address = InetAddress.getByName(host);
      DatagramSocket socket = new DatagramSocket();

      /**
       * Improve your HTTP Client to provide other request Methods(POST, DELETE, …)
       * and also improve to handle headers(Content-Type, User-Agent, …)
       */
      // Mission 2. Fill in #10 Create Request MSG
      // Fill in #10 Transfter to Byte and put in datagramPacket, and set it.
      /* Fill in */;
      String request = "GET " + resource + " HTTP/1.1\r\n" + "Host: " + host + "\r\n" + "Connection: close\r\n\r\n";
      byte[] data = request.getBytes();
      DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
      socket.send(packet);

      // Mission 3. Fill in #11 Initialize byte and datagrampacket to receive data
      /* Fill in */;
      byte[] receiveData = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

      // Fill in #12 Get string from datagrampacket to display
      /* Fill in */;
      socket.receive(receivePacket);

      String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
      System.out.println(response);

      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
