package service.sequencer;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import service.Properties;
import service.domain.InternalRequest;
import service.frontend.FrontEnd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
public class SequencerUdpListener implements Runnable{
    /**
     * Reference to the sequencer singleton
     */
    private final Sequencer sequencer;

    @Override
    public void run() {
        System.out.println("Sequencer is listening to Front end request at port " +
                Properties.SEQUENCER_LISTENING_PORT);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(Properties.SEQUENCER_LISTENING_PORT);
            byte[] buffer;
            DatagramPacket request;
            while (true) {
                System.out.println("3. Sequencer receives FE request");
                buffer = new byte[100000];
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                //getInboundMessage message from rm response, transform to string
                String feRequest = new String(request.getData()).trim();

                //transform from json string to object
                InternalRequest internalRequest = new GsonBuilder().create().fromJson(feRequest, InternalRequest.class);

                //nonce is incremented and synchronized in getNonce(), set it to the incoming message
                internalRequest.setSequencerId(String.valueOf(sequencer.getNonce()));

                //in a new thread send feedback to FE and send request to RM
                new Thread(new SequencerResponder(socket, request,internalRequest, sequencer)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }
}
