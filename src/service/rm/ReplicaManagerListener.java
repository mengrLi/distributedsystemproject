package service.rm;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import service.Properties;
import service.domain.InternalRequest;
import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

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
                replicaManager.log.info("8.1 RM receives message :\n" + seqRequest +"\n");
                if(seqRequest.substring(0,4).equals("miss")){
                    //one RM has error or delay
                    System.err.println(seqRequest);

                    String[] delim = seqRequest.split("-");
                    if(delim.length!=6) System.err.println("THIS SHOULD NOT BE REACHED, INVALID ERROR ALERT MESSAGE");

                    else fixDetectedErrors(delim[1], Integer.parseInt(delim[2]), Long.parseLong(delim[3]));
                    //determine and fix the errors

                }else if(seqRequest.substring(0,4).equals("both")){
                    //two RMs has no responses or delay
                    List<String> inetList = Properties.inetList;

                    boolean[] bArray = new boolean[3];
                    for(int i = 0 ; i < 3 ; ++i) if(bArray[i] = inetList.get(i).equals(replicaManager.getInet())) break;
                    long seqId = Long.parseLong(seqRequest.split("-")[1]);

                    if(bArray[0]){//RM1 is good
                        fixDetectedErrors(Properties.RM_2_INET, Properties.RM_2_LISTENING_PORT, seqId);
                        fixDetectedErrors(Properties.RM_3_INET, Properties.RM_2_LISTENING_PORT, seqId);
                    }else if(bArray[1]){//RM2 is good
                        fixDetectedErrors(Properties.RM_1_INET, Properties.RM_1_LISTENING_PORT, seqId);
                        fixDetectedErrors(Properties.RM_3_INET, Properties.RM_3_LISTENING_PORT, seqId);
                    }else{//RM3 is good
                        fixDetectedErrors(Properties.RM_1_INET, Properties.RM_1_LISTENING_PORT, seqId);
                        fixDetectedErrors(Properties.RM_2_INET, Properties.RM_2_LISTENING_PORT, seqId);
                    }

                }else if(seqRequest.substring(0,8).equals("sequence")){
                    System.out.println("8.4 Error detected RM received error checking message");

                    String[] delim = seqRequest.split("-");
                    long id = Long.parseLong(delim[1]);
                    byte[] data = (replicaManager.getErrorCount()+"-"+replicaManager.getServerResponse(id)).getBytes();
                    DatagramPacket response = new DatagramPacket(
                            data,
                            data.length,
                            rmListenerRequest.getAddress(),
                            rmListenerRequest.getPort()
                    );
                    RmListenerSocket.send(response);
                    replicaManager.log.severe("Responding message ID : " + seqRequest
                            + "\nwith :" + new String(data)+"\n");

                }else if(seqRequest.substring(0,8).equals("increase")) {
                    //both reply from 1 server increase this by 1
                    replicaManager.increaseErrorCount();
                    replicaManager.log.severe("RM increase error count to " + replicaManager.getErrorCount());
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
                    replicaManager.log.info("8.2 RM received seq id " + internalRequest.getId()
                            + "\n" + internalRequest.getClientRequestJson()
                    );
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
    /**
     * Fix the RM at
     * @param errorRmInet Rm address
     * @param errorRmPort Rm port
     * @param sequencerId Seq Id of the error message
     * @throws IOException ioexception
     */
    private void fixDetectedErrors(String errorRmInet, int errorRmPort, long sequencerId) throws IOException{
        //check for error
        System.err.println("8.2 RM received the possible error alert");
        replicaManager.log.severe("RM received the error alert about " +errorRmInet+":"+errorRmPort
                + "\nId: " + sequencerId + "\n");
        //immediately, get a image of the server and nonce
        long tempNonce = replicaManager.getNonce();
        String dvl = replicaManager.getDvlServer().toString();
        String kkl = replicaManager.getKklServer().toString();
        String wst = replicaManager.getWstServer().toString();
        String requestMap = replicaManager.getRecords();

        InetAddress address = InetAddress.getByName(errorRmInet);

        System.err.println("8.3 RM send request for error check (8.4 displays on error RM)");
        replicaManager.log.severe("8.3 send request for error check at " + errorRmInet+":"+errorRmPort
                + "\nabout ID : " + sequencerId +"\n");
        byte[] message = ("sequence-"+sequencerId).getBytes();

        DatagramSocket socket1 = new DatagramSocket();
        DatagramPacket packet1 = new DatagramPacket(message, message.length, address, errorRmPort);
        byte[] response = new byte[100000];
        DatagramPacket reply = new DatagramPacket(response, 100000);
        socket1.send(packet1);
        socket1.setSoTimeout(1500);

        String errorConsequence;

        byte[] errorBytes;

        try{
            socket1.receive(reply);
        }catch (SocketTimeoutException ste){
            System.err.println("8.4-Timeout-1 - SOCKET TIMEOUT");
            errorConsequence = "reboot"
                    +"-/-"+tempNonce
                    +"-/-"+dvl
                    +"-/-"+kkl
                    +"-/-"+wst
                    +"-/-"+requestMap; //threshold reached, kill it!
            System.err.println("8.4-Timeout-2 - RM issues reboot");
            errorBytes = errorConsequence.getBytes();
            packet1 = new DatagramPacket(errorBytes, errorBytes.length, address, errorRmPort);
            socket1.send(packet1);
            System.err.println("8.4-Timeout-3 - RM reboot server order sent");

            replicaManager.log.severe("TIMEOUT FROM " + errorRmInet+":"+errorRmPort
                    + "\n about ID: "+ sequencerId + "\n RESTART MESSAGE SENT\n");
            return;
        }
        String messageFromError = new String(reply.getData()).trim();

        System.out.println("8.5 Checking result received : " + messageFromError);

        replicaManager.log.severe("8.5 Checking result received from "+ errorRmInet+":"+errorRmPort
                +" about ID :" +sequencerId+"\n" + messageFromError +"\n");

        String[] delimError = messageFromError.split("-");

        if (!delimError[1].equals(replicaManager.getServerResponse(sequencerId))) {
            //error occurred

            System.err.println("8.6 RM detects error");
            replicaManager.log.severe("8.6 error detected from message :\n"
                            +delimError[1]
                            +"\nreceived from "+ errorRmInet+":"+errorRmPort +" about ID :" +sequencerId+"\n");
            /*
             Each server
            */
            int currErrorCount = Integer.parseInt(delimError[0]);

            if(currErrorCount<4) {
                errorConsequence = "increase"; //Increase error count
                System.err.println("8.7 RM issues Increase error count");
                replicaManager.log.severe("8.6 RM issues Increase error count "+ errorRmInet+":"+errorRmPort
                        +" about ID :" +sequencerId+"\n");
            }
            else {
                errorConsequence = "reboot"
                        +"-/-"+tempNonce
                        +"-/-"+dvl
                        +"-/-"+kkl
                        +"-/-"+wst
                        +"-/-"+requestMap; //threshold reached, kill it!
                System.err.println("8.7 RM issues reboot");
                replicaManager.log.severe("8.7 RM issues reboot "+ errorRmInet+":"+errorRmPort
                        +" about ID :" +sequencerId+"\n");
            }
            System.err.println("(8.8 displays on the error RM)");

            errorBytes = errorConsequence.getBytes();
            packet1 = new DatagramPacket(errorBytes, errorBytes.length, address, errorRmPort);
            socket1.send(packet1);

            System.err.println((currErrorCount>=4)
                    ? "8.9/ RM reboot server order sent"
                    : "8.9/ RM Increase error count order sent");

        }else{
            //No error
            System.out.println("8.6 RM Error was due to udp delay, servers have given a valid response");
            replicaManager.log.severe("8.6 RM Error was due to udp delay, servers have given a valid response "
                    + errorRmInet+":"+errorRmPort
                    +" about ID :" +sequencerId+"\n");
        }

        //close socket afterward
        socket1.close();
    }
}
