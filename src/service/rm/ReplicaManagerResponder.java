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
    private final DatagramSocket socket;
    private final DatagramPacket request;
    private final ReplicaManager replicaManager;
    private final SequencerId sequencerId;
    private String clientMessage;
    private InternalRequest internalRequest;

    private String responseToFrontEnd = "Error: No response from ";
    private String campusAbrev;

    @Override
    public void run() {
        internalRequest = replicaManager.getInternalMessage(sequencerId.getIdLong());
        //since sequencer does not need response.
        //only need to process this message and forward to the correct server
        clientMessage = internalRequest.getClientRequestJson();
        while(replicaManager.getNonce() != sequencerId.getIdLong()){
        }
        parseInboundMessage();
        forwardMessage();
        sendResponseToFrontEnd(responseToFrontEnd);
        replicaManager.increaseNonce();
    }
    /**
     *  determine campus
     */
    private void parseInboundMessage(){

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
        }
    }

    /**
     * forward message after determined the which campus is needed
     */
    private void forwardMessage() {
        Campus campus = Campus.getCampus(campusAbrev);
        responseToFrontEnd+=campus.name;
        String response = new ReplicaManagerRequest(internalRequest, replicaManager, campus).sendToServer();
        if(response!=null) responseToFrontEnd = response;
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
