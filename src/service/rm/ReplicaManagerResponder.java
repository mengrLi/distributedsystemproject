package service.rm;

import lombok.RequiredArgsConstructor;
import service.domain.InternalRequest;
import service.server.requests.CreateRoomRequest;
import service.server.requests.GetTimeSlotCountRequest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
public class ReplicaManagerResponder implements Runnable {
    private final DatagramSocket socket;
    private final DatagramPacket request;
    private final InternalRequest internalRequest;
    private String clientMessage;

    @Override
    public void run() {
        //since sequencer does not need response.
        //only need to process this message and forward to the correct server
        clientMessage = internalRequest.getClientRequestJson();
        parseInboundMessage();


    }
    /**
     *  determine campus
     */
    private void parseInboundMessage(){

        //need to determine which server the client belongs to
        switch (internalRequest.getMethod()){
            case "create":
                createRoomForwarder();
                break;
            case "delete":
                deleteRoomForwarder();
                break;
            case "book":
                bookRoomForwarder();
                break;
            case "switch":
                switchRoomForwarder();
                break;
            case "count":
                getTimeSlotCountForwarder();
                break;
            case "room":
                getTimeSlotByRoomForwarder();
                break;
            case "cancel":
                cancelBookingForwarder();
                break;
            case "check":
                checkAdminIdForwarder();
                break;
        }
    }
    private void createRoomForwarder() {
    }

    private void deleteRoomForwarder() {
    }

    private void bookRoomForwarder() {

    }

    private void switchRoomForwarder() {

    }

    private void getTimeSlotCountForwarder() {

    }

    private void getTimeSlotByRoomForwarder() {

    }

    private void cancelBookingForwarder() {
    }

    private void checkAdminIdForwarder() {

    }





}
