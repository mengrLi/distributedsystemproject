package service.frontend;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import service.domain.RmResponse;

import java.net.DatagramPacket;

/**
 * Created by PT-PC on 2017-12-01.
 */
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

        frontEnd.addRmResponseToInboundMessage(msgId, rmResponse);
        System.err.println("RM message received from " + rmResponse.getInet());
    }
}
