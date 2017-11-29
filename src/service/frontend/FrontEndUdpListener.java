package service.frontend;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import service.Properties;
import service.domain.RmResponse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
public class FrontEndUdpListener implements Runnable {
    private final FrontEnd frontEnd;
    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            System.out.println("Front End is listening at port " + Properties.FRONTEND_UDP_LISTENING_PORT);
            socket = new DatagramSocket(Properties.FRONTEND_UDP_LISTENING_PORT);
            byte[] buffer;
            DatagramPacket request;
            while (true) {
                buffer = new byte[100000];
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                /**
                 * TODO change to thread..
                 */
                //getInboundMessage message from rm response, transform to string
                String rmResponseJson = new String(request.getData()).trim();
                //transform from json string to object
                RmResponse rmResponse = new GsonBuilder().create().fromJson(rmResponseJson, RmResponse.class);
                String msgId = rmResponse.getSequencerId();

                synchronized(frontEnd.getMapLock()){
                    frontEnd.getMessageBook().getInboundMessage(msgId).addRmResponseToInboundMessage(rmResponse);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }
}
