package service.rm;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import service.domain.InternalRequest;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
        DatagramSocket RmListenerSocket;
        try {
            RmListenerSocket = new DatagramSocket(listeningPort);
            byte[] buffer;
            DatagramPacket rmListenerRequest;
            while(true){
                buffer = new byte[100000];
                rmListenerRequest = new DatagramPacket(buffer, buffer.length);
                RmListenerSocket.receive(rmListenerRequest);

                String seqRequest = new String(rmListenerRequest.getData()).trim();

                //RM receives the error check request
                /*
                ONE IMPORTANT ASSUMPTION HERE, there are always two servers are correct and on time!
                The other cases are not considered (Which is bad...)
                 */
                if(seqRequest.substring(0,4).equals("miss")){
                    System.err.println("Replica manager received the possible error alert");
                    System.out.println(seqRequest);

                    String[] delim = seqRequest.split("-");
                    if(delim.length!=6){
                        System.err.println("THIS SHOULD NOT BE REACHED, INVALID ERROR ALERT MESSAGE");
                    }else{
                        //check for error
                        synchronized (this){
                            //use this object as lock, to stop all message from accessing

                            //immediately, get a image of the server and nonce
                            long tempNonce = replicaManager.getNonce();
                            String dvl = replicaManager.getDvlServer().toString();
                            String kkl = replicaManager.getKklServer().toString();
                            String wst = replicaManager.getWstServer().toString();

                            String errorRmInet = delim[1];
                            InetAddress address = InetAddress.getByName(errorRmInet);
                            int errorRmPort = Integer.parseInt(delim[2]);
                            long sequencerId = Long.parseLong(delim[3]);

                            byte[] message = ("sequence-"+sequencerId).getBytes();

                            DatagramSocket socket1 = new DatagramSocket();
                            DatagramPacket packet1 = new DatagramPacket(message, message.length, address, errorRmPort);
                            socket1.send(packet1);
                            byte[] response = new byte[100000];
                            DatagramPacket reply = new DatagramPacket(response, 100000);
                            socket1.receive(reply);
                            String messageFromError = new String(reply.getData()).trim();

                            String[] delimError = messageFromError.split("-");
                            if (!delimError[0].equals(replicaManager.getServerResponse(sequencerId))) {
                                //error occurred
                                String errorConsequence;
                                byte[] errorBytes;
                                boolean flag = true; // success indicator
                                int count = 0; // try 3 times
                                /*
                                Each server
                                 */
                                int currErrorCount = Integer.parseInt(delimError[1]);
                                while(flag && count<3){
                                    if(currErrorCount<4) errorConsequence = "increase"; //Increase error count
                                    else errorConsequence = "reboot"
                                            +"-/-"+tempNonce
                                            +"-/-"+dvl
                                            +"-/-" +kkl
                                            +"-/-"+wst; //threshold reached, kill it!

                                    errorBytes = errorConsequence.getBytes();
                                    packet1 = new DatagramPacket(errorBytes, errorBytes.length, address, errorRmPort);
                                    socket1.send(packet1);
                                    response = new byte[1000000];
                                    reply = new DatagramPacket(response, 1000000);
                                    socket1.receive(reply);
                                    messageFromError = new String(reply.getData()).trim();

                                    if(currErrorCount<4) {
                                        //if response is not the proper value, increase again
                                        //should not reach normally, unless the server crashed
                                        if(messageFromError.equals("increased")) flag = false;
                                    }else {
                                        //if response is not "killed", double kill or triple kill it,
                                        //should not reach normally, unless the server crashed
                                        if(messageFromError.equals("killed")) flag = false;
                                    }
                                    if(++count==3) System.err.println("Server "+ delim[1]+" has no response");
                                }
                            }else{
                                //No error
                                System.out.println("Error was due to udp delay, servers have given a valid response");
                            }
                        }
                    }
                }else if(seqRequest.substring(0,8).equals("sequence")){
                    String[] delim = seqRequest.split("-");
                    long id = Long.parseLong(delim[1]);
                    byte[] data = replicaManager.getServerResponse(id).getBytes();
                    DatagramPacket response = new DatagramPacket(data, data.length, rmListenerRequest.getAddress(), rmListenerRequest.getPort());
                    RmListenerSocket.send(response);

                }else if(seqRequest.substring(0,8).equals("increase")) {
                    //both reply from 1 server increase this by 1
                    replicaManager.increaseErrorCount();
                    byte[] data = "increased".getBytes();
                    DatagramPacket response = new DatagramPacket(data, data.length, rmListenerRequest.getAddress(), rmListenerRequest.getPort());
                    RmListenerSocket.send(response);

                }else if(seqRequest.substring(0,6).equals("reboot")){
                    //since both good servers send the reboot message, only the first one is performed,
                    //determined by the error count, if ec == 0, server has been restarted
                    if(replicaManager.getErrorCount()>=4){
                        String delim[] = seqRequest.split("-/-");
                        System.err.println("REBOOTING");
                        replicaManager.restartServers(Integer.parseInt(delim[1]), delim[2], delim[3], delim[4]);
                    }
                    //send the confirmation to the good ones
                    if(replicaManager.getErrorCount()==0){
                        byte[] data = "killed".getBytes();
                        DatagramPacket response = new DatagramPacket(data, data.length, rmListenerRequest.getAddress(), rmListenerRequest.getPort());
                        RmListenerSocket.send(response);
                    }
                }else{
                    InternalRequest internalRequest = new GsonBuilder().create().fromJson(seqRequest, InternalRequest.class);

                    System.out.println("Received id " + internalRequest.getId());
                    //save to request list, and increase the nonce counter
                    //note: synchronization is done in the getters
                    replicaManager.putInternalMessage(internalRequest);

                    new Thread(new ReplicaManagerResponder(RmListenerSocket, rmListenerRequest, replicaManager, internalRequest.getSequencerId())).start();

                    System.out.println("Replica Manager has sent the new request " + internalRequest.getId() + " to process");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
