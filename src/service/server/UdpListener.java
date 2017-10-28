package service.server;

import domain.Campus;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
public class UdpListener{
    private final Campus campus;

    public void init(){
        System.out.println(campus.name + " trying to set udp listener");
        udpListening();
        System.out.println(campus.name + " creating upd listener");
    }


    private void udpListening() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(campus.udpPort);
            System.out.println(socket.isConnected());
            byte[] buffer;
            DatagramPacket request;
            while (true) {
                buffer = new byte[100000];
                request = new DatagramPacket(buffer, buffer.length);
                System.out.println(socket.getPort());
                socket.receive(request);
                System.out.println(2);
                new Thread(new UdpResponder(socket, request)).start();
                System.out.println(3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }
}
