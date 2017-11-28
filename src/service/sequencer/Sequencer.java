package service.sequencer;

import com.google.gson.GsonBuilder;
import domain.Lock;
import service.Properties;
import service.domain.RmResponse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton
 */
public class Sequencer implements Runnable{
    public static Sequencer ourInstance = new Sequencer();
    private static long nonce = 0;
    private static List<InternalRequest> internalRequestList;

    private final Lock nonceLock = new Lock();

    /**
     * Constructor
     */
    private Sequencer() {
        internalRequestList = new LinkedList<>();
        Thread thread = new Thread(this);
        thread.run();
        System.out.println("Sequencer initialized");
    }

    @Override
    public void run(){
        initUdpListeningPort();
    }

    private void initUdpListeningPort(){
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(Properties.SEQUENCER_LISTENING_PORT);
            byte[] buffer;
            DatagramPacket request;
            while (true) {
                buffer = new byte[100000];
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);


                /**
                 * Move to responder
                 */
                //getInboundMessage message from rm response, transform to string
                String feRequest = new String(request.getData());

                //transform from json string to object
                InternalRequest internalRequest = new GsonBuilder().create().fromJson(feRequest, InternalRequest.class);

                synchronized(this.nonceLock){
                    ++nonce;
                    internalRequest.setSequencerId(String.valueOf(nonce));
                }
                /**
                 * end of move
                 */

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }
}
