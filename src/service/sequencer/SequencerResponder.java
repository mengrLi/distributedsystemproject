package service.sequencer;

import lombok.RequiredArgsConstructor;
import service.domain.InternalRequest;
import service.frontend.FrontEnd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
public class SequencerResponder implements Runnable{
    private final DatagramSocket socket;
    private final DatagramPacket request;
    private final InternalRequest internalRequest;
    private final Sequencer sequencer;

    @Override
    public void run(){
        System.out.println("4. Sequencer responder is processing the new incoming message");

        // 1 tell FE about the seq ID
        // 2 tell RM about the new message
        try{
            //reply the sequencer id
            byte[] data = internalRequest.getSequencerId().getId().getBytes();
            DatagramPacket response = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
            socket.send(response);
            String info = "5. Sequencer responder replies to FE for seq Id";
            sequencer.log.info(info+"\nGiven new Id "+ internalRequest.getId()
                    +"\nto message :"+ internalRequest.getClientRequestJson()+"\n");
            System.out.println(info);

            //tell RM about the new message
            new SequencerUdpRequest(internalRequest).sendRequest();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
