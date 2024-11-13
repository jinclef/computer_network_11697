import java.io.*;
import java.net.*;
import java.util.*;

public final class UDPWebServer {
    public static void main(String argv[]) throws Exception {
        // Mission 1. Fill in #1 Create DatagramSocket.
        // Changed ServerSocket to DatagramSocket
        DatagramSocket serverSocket = new DatagramSocket(8888);

        // Process HTTP service requests in an infinite loop.
        while (true) {
            // Mission 1, Fill in #2 Init receiveData
            // Fill in #2 Construct an object to process the HTTP request message.(Changed
            // to DatagramPacket)

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // Fill in #3 Listen for a UDP packet.
            // Fill in #3 Construct an object to process the HTTP request message(From
            // changed constructor)
            /* Fill in */;
            serverSocket.receive(receivePacket);

            UDPHttpRequest request = new UDPHttpRequest(serverSocket, receivePacket); // Changed
                                                                                      // constructor
            // Create a new thread to process the request.
            Thread thread = new Thread(request);

            // Start the thread.
            thread.start();
        }
    }
}
