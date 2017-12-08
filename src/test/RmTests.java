package test;

import service.Properties;
import service.domain.InternalRequest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * RM processing order test
 */
public class RmTests {

    public static void main(String[] args) {
        InternalRequest internalRequest3 = new InternalRequest("orderT1est", "testMessageString3");
        internalRequest3.setSequencerId(String.valueOf(3));

        InternalRequest internalRequest2 = new InternalRequest("orderTest", "testMessageString2");
        internalRequest2.setSequencerId(String.valueOf(2));

        InternalRequest internalRequest1 = new InternalRequest("orderTest", "testMessageString1");
        internalRequest1.setSequencerId(String.valueOf(1));

        try{
            //send 3

            byte[] message = internalRequest3.toString().getBytes();
            int length = message.length;
            InetAddress address = InetAddress.getByName(Properties.RM_1_INET);
            DatagramPacket packet = new DatagramPacket(message, length, address, Properties.RM_1_LISTENING_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            System.out.println("3 sent");

            //send 2
            message = internalRequest2.toString().getBytes();
            length = message.length;
            address = InetAddress.getByName(Properties.RM_1_INET);
            packet = new DatagramPacket(message, length, address, Properties.RM_1_LISTENING_PORT);
            socket = new DatagramSocket();
            socket.send(packet);
            System.out.println("2 sent");

            //send 1
            message = internalRequest1.toString().getBytes();
            length = message.length;
            address = InetAddress.getByName(Properties.RM_1_INET);
            packet = new DatagramPacket(message, length, address, Properties.RM_1_LISTENING_PORT);
            socket = new DatagramSocket();
            socket.send(packet);

            System.out.println("1 sent");

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
