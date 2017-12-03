package service.frontend;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import service.domain.RmResponse;

import java.net.DatagramPacket;

@RequiredArgsConstructor
public class FrontEndResponder implements Runnable {
    private final DatagramPacket request;
    private final FrontEnd frontEnd;
    @Override
    public void run() {
        //getInboundMessage message from rm response, transform to string
        String rmResponseJson = new String(request.getData()).trim();
        //transform from json string to object
        RmResponse rmResponse = new GsonBuilder().create().fromJson(rmResponseJson, RmResponse.class);
        String msgId = rmResponse.getSequencerId();

        frontEnd.addRmResponseToInboundMessageFE(msgId, rmResponse);
        System.out.println("10 RM message received from " + rmResponse.getInet());
    }
}
