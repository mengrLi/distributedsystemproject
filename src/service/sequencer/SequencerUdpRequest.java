package service.sequencer;

import lombok.RequiredArgsConstructor;
import service.Properties;
import service.domain.InternalRequest;

import java.io.IOException;
import java.net.*;

/**
 * Sequencer sends sequenced message to all replica messages, no response is needed from the RMs
 * Thus a one way udp message is needed
 */
@RequiredArgsConstructor
public class SequencerUdpRequest {
    private final InternalRequest internalRequest;
    private final String rm1Address = Properties.RM_1_INET;
    private final String rm2Address = Properties.RM_2_INET;
    private final String rm3Address = Properties.RM_3_INET;

    private final int rm1Port = Properties.RM_1_LISTENING_PORT;
    private final int rm2Port = Properties.RM_2_LISTENING_PORT;
    private final int rm3Port = Properties.RM_3_LISTENING_PORT;

    private final String rm1Name = Properties.rm1Name;
    private final String rm2Name = Properties.rm2Name;
    private final String rm3Name = Properties.rm3Name;

    /**
     * Send request to each RM
     */
    public void sendRequest(){
        sendToRm(rm1Address, rm1Port, rm1Name);
        sendToRm(rm2Address, rm2Port, rm2Name);
        sendToRm(rm3Address, rm3Port, rm3Name);
    }

    /**
     * Send to RMs, currently, no response is needed from the RMs. To be determined whether response is needed
     * @param rmAddress rm address
     * @param rmPort rm port
     * @param serverName rm name
     */
    private void sendToRm(String rmAddress, int rmPort, String serverName){
        byte[] messageInByte = internalRequest.toString().getBytes();
        int length = messageInByte.length;
        DatagramSocket socket;
        try{
            System.out.println("7. Sequencer is sending message to "+ serverName + " at " + rmAddress +":"+rmPort);
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(rmAddress);
            DatagramPacket request = new DatagramPacket(messageInByte, length, address, rmPort);
            socket.send(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
