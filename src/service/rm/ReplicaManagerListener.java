package service.rm;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import service.domain.InternalRequest;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
public class ReplicaManagerListener implements Runnable {
    /**
     * Reference to the replica manager
     */
    private final ReplicaManager replicaManager;
    private final int listeningPort;

    @Override
    public void run() {
        System.out.println("Replica manager is listening to Sequencer request at port " + listeningPort);
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(listeningPort);
            byte[] buffer;
            DatagramPacket request;
            while(true){
                buffer = new byte[100000];
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String seqRequest = new String(request.getData()).trim();

                if(seqRequest.equals("reboot")){
                    String dvl = replicaManager.getDvlServer().toString();
                    String kkl = replicaManager.getKklServer().toString();
                    String wst = replicaManager.getWstServer().toString();

                    System.err.println("reboot");

                    replicaManager.restartServers(replicaManager.getNonce(), dvl, kkl, wst);

                }else{
                    InternalRequest internalRequest = new GsonBuilder().create().fromJson(seqRequest, InternalRequest.class);

                    System.out.println("Received id " + internalRequest.getId());
                    //save to request list, and increase the nonce counter
                    //note: synchronization is done in the getters
                    replicaManager.putInternalMessage(internalRequest);

                    new Thread(new ReplicaManagerResponder(socket, request, replicaManager, internalRequest.getSequencerId())).start();

                    System.out.println("Replica Manager has sent the new request " + internalRequest.getId() + " to process");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
