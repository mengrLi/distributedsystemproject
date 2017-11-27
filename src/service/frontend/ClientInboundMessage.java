package service.frontend;

import domain.Lock;
import domain.SequencerId;
import service.Properties;
import service.domain.RmResponse;
import service.rm.ReplicaManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

/**
 1. parse json to determine which campus
 2. send to sequencer, local udp
 3. listen to response from RM
 4. determine correctness
 5. return to client
 */
public class ClientInboundMessage implements Runnable{
    /**
     * Json format incoming message from client
     */
    private final String inboundMessage;
    /**
     * The method of which this message should be processed
     */
    private final String method;
    /**
     * A reference of the Singleton front end
     */
    private final FrontEnd frontEnd;
    /**
     * Sequencer Id of this message
     */
    private SequencerId sequencerId;
    /**
     * Message to be returned to client
     */
    private String returnMessage = null;
    /**
     * Message received time
     */
    private final long receiveTime;
    /**
     * First response from RM's time
     */
    private long firstResponseTime = 0;
    /**
     * Initially set to 10 sec, change to 2 times of the first responses time upon receiving the 1st msg
     */
    private long timeOutTime = 0;
    /**
     * The list of three RM response
     */
    public List<RmResponse> rmResponseList;

    public final Lock rrLock = new Lock();

    /**
     * Constructor
     * Initiate received time and time out max limit to 10 sec
     * @param inboundMessage json format client request
     * @param method method of which the client request should be processed
     * @param frontEnd reference to the singleton front end
     */
    public ClientInboundMessage(String inboundMessage, String method, FrontEnd frontEnd){
        this.inboundMessage = inboundMessage;
        this.method = method;
        this.frontEnd = frontEnd;
        this.receiveTime = System.currentTimeMillis();
        this.timeOutTime = receiveTime+10000;//max waiting 10 sec

        //instantiate Replica manager response list. max size 3
        rmResponseList = new LinkedList<>();
    }

    /**
     * Only public method
     * Process the message
     * @return json string to be returned to client
     */
    String process(){
        sendToSequencer();
        Thread thread = new Thread(this);
        thread.run();
        //return when thread finish running
        return returnMessage;
    }

    /**
     * Thread Run
     * 1. wrap method with client request
     * 2. forward to sequencer
     * 3. get sequencer ID
     * 4. Process RM response when one of the two conditions fulfilled
     */
    @Override
    public void run() {
        sendToSequencer();
        if(!returnMessage.startsWith("Error")){
            while(System.currentTimeMillis()<timeOutTime && rmResponseList.size()!=3){
                //hold thread before one of the condition reaches
            }
            processReturnData();
        }
    }

    /**
     * This step should be a linear process, no thread is needed, i.e. should not proceed to the next step without a
     * response from sequencer
     * Save this InboundMessage to frontend's message book
     */
    private void sendToSequencer(){
        //udp call and get Inbound Message's sequencerId
        byte[] messageInByte = inboundMessage.getBytes();
        int length = messageInByte.length;
        DatagramSocket socket;
        try {
            System.out.println("Forwarding request from Front End to Sequencer");
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(Properties.LOCALHOST);
            DatagramPacket request = new DatagramPacket(messageInByte, length, address, Properties.SEQUENCER_LISTENING_PORT);
            socket.send(request);
            byte[] buffer = new byte[100000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
            sequencerId = new SequencerId(new String(reply.getData()).trim());
            synchronized (frontEnd.getMapLock()){
                frontEnd.getMessageBook().put(sequencerId.getId(), this);
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnMessage = "Error: I/O Exception";
        }
    }

    /**
     * process the data returned by RM
     */
    private void processReturnData(){
        RmResponse r1 = null;
        RmResponse r2 = null;
        RmResponse r3 = null;

        int responseCount;
        /*
        At this point, all responses must be on time, because the moment this method is called either all received
        or time out limit have been reached
        Except, if a lock delayed the process, but in this case, I feel ok to include this response
         */
        synchronized (this.rrLock) {
            responseCount = rmResponseList.size();

            if(responseCount > 0) r1 = rmResponseList.get(0);
            if(responseCount > 1) r2 = rmResponseList.get(1);
            if(responseCount > 2) r3 = rmResponseList.get(2);
        }
        returnMessage = "Error : Server time out";
        if(responseCount == 0){
            //no responses, server recover?
            alertMistakes();
        }else if(responseCount == 1){
            //only one gave response, check
            alertMistakes(r1.getReplicaManager());
        }else if(responseCount == 2){
            //2 responses
            if(!r1.getResponseMessage().equals(r2.getResponseMessage())){
                //not equal + 1 not on time, => server recover
                alertMistakes();
            }else{
                //both match, confident
                alertMistakes(r1.getReplicaManager(), r2.getReplicaManager());
                returnMessage = r1.getResponseMessage();
            }
        }else{
            //3 responses
            if(r1.getResponseMessage().equals(r2.getResponseMessage())
                    &&r2.getResponseMessage().equals(r3.getResponseMessage())
                    &&r1.getResponseMessage().equals(r3.getResponseMessage())){ //all equal, best case
                returnMessage = r1.getResponseMessage();

            }else if(r1.getResponseMessage().equals(r2.getResponseMessage())){ //1 and 2 match
                returnMessage = r1.getResponseMessage();
                alertMistakes(r1.getReplicaManager(), r2.getReplicaManager());

            }else if(r1.getResponseMessage().equals(r3.getResponseMessage())){ //1 and 3 match
                returnMessage = r1.getResponseMessage();
                alertMistakes(r1.getReplicaManager(), r3.getReplicaManager());

            }else if(r2.getResponseMessage().equals(r3.getResponseMessage())){ //2 and 3 match
                returnMessage = r2.getResponseMessage();
                alertMistakes(r2.getReplicaManager(), r3.getReplicaManager());

            }else{
                //no match in three, server recover
                alertMistakes();
            }
        }
    }

    /**
     * Alert mistake has been detected
     * @param replicaManagers the corrected ones !!!!!
     */
    private void alertMistakes(ReplicaManager ... replicaManagers){
        //TODO
    }
    /*
     * determine campus
     */
//    private void parseInboundMessage(){
//        switch (method){
//            case "create":
//                break;
//            case "delete":
//                break;
//            case "book":
//                break;
//            case "switch":
//                break;
//            case "count":
//                break;
//            case "room":
//                break;
//            case "cancel":
//                break;
//            case "check":
//                break;
//        }
//    }


    /**
     * add response to list ONLY
     * process data "go" signal is given by thread run()
     * This method determines whether a response is on time or not ONLY
     * @param rmResponse the response to be saved
     */
    void addRmResponseToInboundMessage(RmResponse rmResponse){
        synchronized (this.rrLock){
            if(rmResponseList.size()==0){
                //first insert response
                firstResponseTime = System.currentTimeMillis();
                timeOutTime = firstResponseTime + 2 * (firstResponseTime - receiveTime);
                rmResponse.setOnTime(true);
            }else{
                if(System.currentTimeMillis() < timeOutTime) rmResponse.setOnTime(true);//on time
                else rmResponse.setOnTime(false); // late
            }
            rmResponseList.add(rmResponse);
        }
    }










}
