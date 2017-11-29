package service.rm;

import domain.Campus;
import lombok.RequiredArgsConstructor;
import service.Properties;
import service.domain.InternalRequest;

import java.io.IOException;
import java.net.*;

@RequiredArgsConstructor
public class ReplicaManagerRequest {
    private final InternalRequest internalRequest;
    private final ReplicaManager replicaManager;
    private final Campus campus;

    /**
     * Send message to the correct server where id resides
     * @return server processed response
     */
    public String sendToServer(){
        byte[] data = internalRequest.toString().getBytes();
        int length = data.length;
        DatagramSocket socket;
        try{
            System.out.println(replicaManager.getRmName() + " is forwarding client message to " + campus.name);
            socket = new DatagramSocket();
            //server and rm are in same localhost
            InetAddress address = InetAddress.getByName(Properties.LOCALHOST);
            DatagramPacket request = new DatagramPacket(data, length, address, campus.rmPort);
            socket.send(request);

            //wait for response
            byte[] buffer = new byte[100000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
            String answer = new String(reply.getData()).trim();
            System.out.println("RM request got response ---" + answer);
            return answer;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: IO exception at " + ReplicaManagerRequest.class;
        }

    }
}
