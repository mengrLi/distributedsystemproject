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
                System.out.println("8.1 RM receives a message");
                if(seqRequest.substring(0,4).equals("miss")){

                    System.out.println(seqRequest);

                    String[] delim = seqRequest.split("-");
                    if(delim.length!=6){
                        System.err.println("THIS SHOULD NOT BE REACHED, INVALID ERROR ALERT MESSAGE");
                    }else{
                        //check for error
                        System.err.println("8.2 RM received the possible error alert");
                        //immediately, get a image of the server and nonce
                        long tempNonce = replicaManager.getNonce();
                        String dvl = replicaManager.getDvlServer().toString();
                        String kkl = replicaManager.getKklServer().toString();
                        String wst = replicaManager.getWstServer().toString();
                        String requestMap = replicaManager.getRecords();

                        String errorRmInet = delim[1];
                        InetAddress address = InetAddress.getByName(errorRmInet);
                        int errorRmPort = Integer.parseInt(delim[2]);
                        long sequencerId = Long.parseLong(delim[3]);

                        System.err.println("8.3 RM send request for error check (8.4 displays on error RM)");
                        byte[] message = ("sequence-"+sequencerId).getBytes();

                        DatagramSocket socket1 = new DatagramSocket();
                        DatagramPacket packet1 = new DatagramPacket(message, message.length, address, errorRmPort);
                        socket1.send(packet1);
                        byte[] response = new byte[100000];
                        DatagramPacket reply = new DatagramPacket(response, 100000);
                        socket1.receive(reply);
                        String messageFromError = new String(reply.getData()).trim();

                        System.out.println("8.5 Checking result received : " + messageFromError);

                        String[] delimError = messageFromError.split("-");
                        System.out.println("8.5.1 delimError size : " + delimError.length);
                        if (!delimError[1].equals(replicaManager.getServerResponse(sequencerId))) {
                            //error occurred
                            String errorConsequence;
                            byte[] errorBytes;
                            System.err.println("8.6 RM detects error");
                            /*
                            Each server
                             */
                            int currErrorCount = Integer.parseInt(delimError[0]);

                            if(currErrorCount<4) {
                                errorConsequence = "increase"; //Increase error count
                                System.err.println("8.7 RM issues Increase error count");
                            }
                            else {
                                errorConsequence = "reboot"
                                        +"-/-"+tempNonce
                                        +"-/-"+dvl
                                        +"-/-"+kkl
                                        +"-/-"+wst
                                        +"-/-"+requestMap; //threshold reached, kill it!
                                System.err.println("8.7 RM issues reboot");
                            }
                            System.err.println("8.8 displays on error RM");

                            errorBytes = errorConsequence.getBytes();
                            packet1 = new DatagramPacket(errorBytes, errorBytes.length, address, errorRmPort);
                            socket1.send(packet1);

                            System.err.println((currErrorCount>=4)
                                    ? "8.9 RM reboot server order sent"
                                    : "8.9 RM Increase error count order sent");
                        }else{
                            //No error
                            System.out.println("8.6 RM Error was due to udp delay, servers have given a valid response");
                        }

                    }
                }else if(seqRequest.substring(0,8).equals("sequence")){
                    System.out.println("8.4 Error detected RM received error message");

                    String[] delim = seqRequest.split("-");
                    long id = Long.parseLong(delim[1]);
                    byte[] data = (replicaManager.getErrorCount()+"-"+replicaManager.getServerResponse(id)).getBytes();
                    DatagramPacket response = new DatagramPacket(data, data.length, rmListenerRequest.getAddress(), rmListenerRequest.getPort());
                    RmListenerSocket.send(response);

                }else if(seqRequest.substring(0,8).equals("increase")) {
                    //both reply from 1 server increase this by 1
                    replicaManager.increaseErrorCount();

                    System.err.println("8.8 Current Error Count is : "+replicaManager.getErrorCount()/2);

                }else if(seqRequest.substring(0,6).equals("reboot")){
                    //since both good servers send the reboot message, only the first one is performed,
                    //determined by the error count, if ec == 0, server has been restarted

                    System.err.println("8.8 REBOOT REQUEST RECEIVED");

                    if(replicaManager.getErrorCount()==0) System.out.println("8.8.1 killed by the other campus");


                    if(replicaManager.getErrorCount()>=4){
                        String delim[] = seqRequest.split("-/-");
                        System.err.println("8.8.1 REBOOTING (8.8.2 - 8.8.4 displays on error server)");
                        replicaManager.restartServers(Integer.parseInt(delim[1]), delim[2], delim[3], delim[4]);
                        replicaManager.loadRequestMap(delim[5]);

                        System.out.println("8.8.5 Rebooted");
                    }
                }else{
                    InternalRequest internalRequest = new GsonBuilder().create().fromJson(seqRequest, InternalRequest.class);

                    System.out.println("8.2 RM received seq id " + internalRequest.getId());
                    //save to request list, and increase the nonce counter
                    //note: synchronization is done in the getters
                    replicaManager.putInternalMessage(internalRequest);

                    new Thread(new ReplicaManagerResponder(replicaManager, internalRequest.getSequencerId())).start();

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
