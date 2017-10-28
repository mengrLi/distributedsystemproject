package service.server;

import domain.Campus;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class UdpRequest{
    private final Server server;
    private final String request;
    private final Campus campus;

    public String sendRequest() {
        byte[] messageInByte = request.getBytes();
        int length = messageInByte.length;
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(messageInByte, length, address, campus.udpPort);
            socket.send(request);
            byte[] buffer = new byte[100000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
            return new String(reply.getData()).trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: I/O Exception";
        }
    }


}

