package service.server;

import lombok.RequiredArgsConstructor;
import service.domain.InternalRequest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
public class ServerResponder implements Runnable {
    private final DatagramSocket socket;
    private final DatagramPacket request;
    private final InternalRequest internalRequest;
    private final Server server;

    @Override
    public void run() {
        //process client request in the campus servers
        byte[] data = makeResponse();
        //send data back
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] makeResponse(){
        String json = internalRequest.getClientRequestJson();
        String response = "Error: Server cannot be reached";

        switch (internalRequest.getMethod()){
            //for each method, call server.method directly
            case "create":
                response = server.createRoom(json);
                break;
            case "delete":
                response = server.deleteRoom(json);
                break;
            case "book":
                response = server.bookRoom(json);
                break;
            case "switch":
                response = server.switchRoom(json);
                break;
            case "count":
                response = server.getAvailableTimeSlotCount(json);
                break;
            case "room":
                response = server.getAvailableTimeSlotByRoom(json);
                break;
            case "cancel":
                response = server.cancelBooking(json);
                break;
            case "check":
                response = String.valueOf(server.checkAdminId(json));
                break;
            //return the response to RM as json string (like the assignment 2 format)
        }
        return response.getBytes();
    }
}
