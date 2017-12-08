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
            System.out.println("8.6 "+replicaManager.getRmName() + " is forwarding client message to " + campus.name);
            socket = new DatagramSocket();
            //server and rm are in same localhost
            InetAddress address = InetAddress.getByName(Properties.LOCALHOST);
            DatagramPacket request = new DatagramPacket(data, length, address, campus.rmPort);
            byte[] buffer = new byte[100000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            socket.send(request);
            socket.setSoTimeout(Properties.maxUdpWaitingTime);
            //wait for response for 1 sec
            try{
                socket.receive(reply);
            }catch (SocketTimeoutException ste){
                System.err.println("8.7 "+ replicaManager.getRmName()+" request from " + campus.name + " TIMEOUT");
                return "Error : server time out";
            }
            String answer = new String(reply.getData()).trim();
            System.out.println("8.7 "+ replicaManager.getRmName()+" request got response from " + campus.name);
            return answer;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error : IO exception at " + ReplicaManagerRequest.class;
        }

    }
}
