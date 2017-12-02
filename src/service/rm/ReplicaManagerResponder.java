package service.rm;

import domain.BookingInfo;
import domain.Campus;
import domain.SequencerId;
import lombok.RequiredArgsConstructor;
import service.Properties;
import service.domain.InternalRequest;
import service.domain.RmResponse;
import service.server.requests.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@RequiredArgsConstructor
public class ReplicaManagerResponder implements Runnable {
//    private final DatagramSocket socket;
//    private final DatagramPacket request;
    private final ReplicaManager replicaManager;
    private final SequencerId sequencerId;
    private String clientMessage;
    private InternalRequest internalRequest;

    private String responseToFrontEnd = "Error: No response from ";
    private String campusAbrev;


    @Override
    public void run() {
        internalRequest = replicaManager.getInternalMessage(sequencerId.getIdLong());
        System.out.println("8.3 RM responder processing " + internalRequest.getId());

        //since sequencer does not need response.
        //only need to process this message and forward to the correct server
        clientMessage = internalRequest.getClientRequestJson();

        int counter = 0;
        int releaseAt = 100;
        long currNonce = replicaManager.getNonce();

        /*
        ISSUE : MANY REQUEST WAITING, NEED TO WAIT 100 TIMES FOR EACH OF THOSE REQUEST. TODO FIX THIS
         */
        while(currNonce < sequencerId.getIdLong() && counter<releaseAt){
            currNonce = replicaManager.getNonce();
            //if nonce smaller than seq ID wait
            //if nonce == seq ID process
            //if nonce > seq ID duplicate
            System.out.println("8.4 waiting for nonce counter to reach " + internalRequest.getId()
                    + " - current nonce : " + currNonce);
//            System.err.println("trial " + counter);
            ++counter;
            //try 100 times, to release block in case of loss of packet
            if(counter==releaseAt){
                //put an error in that slot
                System.err.println("8.4.1 message is lost, empty message added to " + replicaManager);
                InternalRequest missingMessage = new InternalRequest("missing", "missing message");
                missingMessage.setSequencerId(String.valueOf(currNonce));
                replicaManager.putInternalMessage(missingMessage);
                replicaManager.increaseNonce();
                System.err.println("8.4.2 " + currNonce + " is released without a message - packet lost");
            }
        }
        if(replicaManager.getNonce() > internalRequest.getId()){
            //DUPLICATE MESSAGE!
            System.err.println("8.4 Duplicate Message Received- Message Dropped");
        }else{
            //== CASE
            System.err.println("8.5 processing current nonce " + currNonce);
            parseInboundMessage();
            forwardMessage();
            sendResponseToFrontEnd(responseToFrontEnd);

            replicaManager.increaseNonce();
        }
    }
    /**
     *  determine campus
     */
    private void parseInboundMessage(){
        System.out.println("8.5.1 parse inbound message");

        //need to determine which server the client belongs to
        switch (internalRequest.getMethod()){
            case "create":
                campusAbrev = CreateRoomRequest.parseRequest(clientMessage).getFullID().substring(0,3);
                break;
            case "delete":
                campusAbrev = DeleteRoomRequest.parseRequest(clientMessage).getFullID().substring(0,3);
                break;
            case "book":
                campusAbrev = BookRoomRequest.parseRequest(clientMessage).getCampusOfId().abrev;
                break;
            case "switch":
                campusAbrev = BookingInfo.decode(SwitchRoomRequest.parseRequest(clientMessage)
                        .getBookingID()).getStudentCampusAbrev();
                break;
            case "count":
                campusAbrev = "DVL";
                break;
            case "room":
                campusAbrev = GetTimeSlotByRoomRequest.parseRequest(clientMessage).getCampus().abrev;
                break;
            case "cancel":
                campusAbrev = CancelBookingRequest.parseRequest(clientMessage).getCampus().abrev;
                break;
            case "check":
                campusAbrev = CheckAdminIdRequest.parseRequest(clientMessage).getFullID().substring(0,3);
                break;
            case "missing":
                //TODO how to fix this
                campusAbrev = "ERROR";
                break;
        }
    }

    /**
     * forward message after determined the which campus is needed
     *
     * save the server responses to the message map
     */
    private void forwardMessage() {
        if(campusAbrev.equals("ERROR")){
            System.err.println("8.6 ERROR message missing");
            replicaManager.saveResponseMessage(internalRequest.getId(), "Error : Message Missing");
        }else{
            Campus campus = Campus.getCampus(campusAbrev);
            responseToFrontEnd+=campus.name;

            System.out.println(8.);



            String response = new ReplicaManagerRequest(internalRequest, replicaManager, campus).sendToServer();
            if(response!=null) {
                responseToFrontEnd = response;

                //ERROR TEST!!! THIS MUST SET TO THE ERROR PRODUCING SERVER MANUALLY
                if(replicaManager.getErrorTest()){

                    responseToFrontEnd+="ErrorOccured";
                }
                if(replicaManager.getDelayTest()){
                    try {
                        Thread.currentThread().wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                replicaManager.saveResponseMessage(internalRequest.getId(), responseToFrontEnd);
            }
        }
    }

    /**
     * One way message to FE, no response needed
     * @param responseToFrontEnd message to be sent to front end
     */
    private void sendResponseToFrontEnd(String responseToFrontEnd) {
        System.out.println("sending to response to fe : ");

        RmResponse rmResponse = new RmResponse(replicaManager.getInet(), replicaManager.getRmListeningPort(),
                sequencerId.getId(), responseToFrontEnd);

        byte[] data = rmResponse.toString().getBytes();
        int length = data.length;
        DatagramSocket socket;
        try{
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(Properties.FRONTEND_INET);
            DatagramPacket request = new DatagramPacket(data, length, address, Properties.FRONTEND_UDP_LISTENING_PORT);
            socket.send(request);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
