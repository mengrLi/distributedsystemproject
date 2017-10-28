package test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {
    UDPServer() {
    }


    public static void main(String[] args) {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(5860);
            byte[] buffer = new byte[1000];
            while (true) {
                System.out.println("init");
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                System.out.println("request received");
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
                socket.send(reply);


//                new Thread(new UdpResponder(socket, request, Campus.DORVAL)).start();

            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }
}
