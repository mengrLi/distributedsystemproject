package service.frontend;

import lombok.RequiredArgsConstructor;
import service.domain.RmResponse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * When a mistake is detected,
 */
@RequiredArgsConstructor
public class FrontEndAlert implements Runnable {
    private final RmResponse[] responses;

    @Override
    public void run() {
        if(responses.length == 0){
            //all bad



        }else if(responses.length == 1){
            //one good

            //TODO TEMP
            //test for replicate server function
            String inet = responses[0].getInet();
            int port = responses[0].getRmPort();

            String message = "reboot";
            byte[] mByte = message.getBytes();

            try {
                System.err.println("Alerting RM about the error");
                DatagramSocket socket = new DatagramSocket();
                InetAddress address = InetAddress.getByName(inet);
                DatagramPacket packet = new DatagramPacket(mByte, mByte.length, address, port);

                socket.send(packet);

            }catch (IOException e){
                e.printStackTrace();
            }

        }else{
            //two goods
        }
    }

    private void oneWayMessage(String inet, int port){
//        String message = "mistake"
    }
}
