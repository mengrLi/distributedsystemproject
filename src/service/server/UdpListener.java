package service.server;

import domain.Campus;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
public class UdpListener implements Runnable {
    private final Campus campus;
    private final Server server;

    private void udpListening() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(campus.udpPort);
            byte[] buffer;
            DatagramPacket request;
            while (true) {
                buffer = new byte[100000];
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                new Thread(new UdpResponder(socket, request, server)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }

    @Override
    public void run() {
        System.out.println(campus.name + " setting udp listener");
        udpListening();
        System.out.println(campus.name + " creating upd listener");
    }
}
