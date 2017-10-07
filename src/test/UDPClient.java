package test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            StringBuffer string = new StringBuffer();
            string.append("hi");

            byte[] m = string.toString().getBytes();

            InetAddress address = InetAddress.getByName("localhost");
            System.out.println("Inet address is : " + address);
            int serverPort = 5860;

            DatagramPacket request = new DatagramPacket(m, string.toString().length(), address, serverPort);
            socket.send(request);
            byte[] buffer = new byte[10000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
            System.out.println("Reply : " + new String(reply.getData()));
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }
}
