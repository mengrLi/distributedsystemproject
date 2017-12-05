package service.frontend;

import domain.Lock;
import domain.SequencerId;
import jdk.nashorn.internal.ir.ForNode;
import service.Properties;
import service.domain.RmResponse;
import service.domain.InternalRequest;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

/**
 1. parse json to determine which campus
 2. send to sequencer, local udp
 3. listen to response from RM
 4. determine correctness
 5. return to client
 */
public class ClientInboundMessage{
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
    private String returnMessage = "";
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

    private final String DEFAULT_ERROR = "Error : Server time out";

    /**
     * Constructor
     * Initiate received time and time out max limit to 1 sec
     * @param inboundMessage json format client request
     * @param method method of which the client request should be processed
     * @param frontEnd reference to the singleton front end
     */
    public ClientInboundMessage(String inboundMessage, String method, FrontEnd frontEnd){
        this.inboundMessage = inboundMessage;
        this.method = method;
        this.frontEnd = frontEnd;
        this.receiveTime = System.currentTimeMillis();
        this.timeOutTime = receiveTime+Properties.timeOutLimit;//max waiting 10 sec

        //instantiate Replica manager response list. max size 3
        rmResponseList = new LinkedList<>();
    }

    /**
     * Thread Run
     * 1. wrap method with client request
     * 2. forward to sequencer
     * 3. get sequencer ID
     * 4. Process RM response when one of the two conditions fulfilled
     * Only public method
     * Process the message
     * @return json string to be returned to client
     */
    String process(){

        sendToSequencer();

        if(!returnMessage.startsWith("Error")){
            boolean flag = true;
            while(flag){
                //hold thread before one of the condition reaches
                int size;
                synchronized (rrLock){
                    size = rmResponseList.size();
                }
                if(Properties.singlePcTest && size==1){
                    flag = false;
                    System.err.println("10. TESTING EARLY-RELEASE");
                }
                else if(!Properties.singlePcTest && size==3){
                    flag = false;
                    System.out.println("10. All responses received, early-release");
                }
                else if(System.currentTimeMillis()>timeOutTime) {
                    flag = false;
                    System.err.println("10. TIME OUT, FORCE RELEASE");
                }
            }
            processReturnData();
        }
        //return when thread finish running
        System.out.println("12/// response to client : " + returnMessage);
        frontEnd.log.info("Message id : " + sequencerId.getId() + "\n"
                + "Message detail : " + returnMessage );
        return returnMessage;
    }

    /**
     * This step should be a linear process, no thread is needed, i.e. should not proceed to the next step without a
     * response from sequencer
     * Save this InboundMessage to frontend's message book
     */
    private void sendToSequencer(){
        //udp call and get Inbound Message's sequencerId
        //create internal request object
        InternalRequest internalRequest = new InternalRequest(method, inboundMessage);
        //to json and to byte[]
        byte[] messageInByte = internalRequest.toString().getBytes();
        //get length of the message
        int length = messageInByte.length;
        DatagramSocket socket;
        try {
            String info = "2. Forwarding request from Front End to Sequencer";
            frontEnd.log.info("\n"+info+"\n");
            System.out.println(info);
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(Properties.LOCALHOST);
            DatagramPacket request = new DatagramPacket(messageInByte, length, address, Properties.SEQUENCER_LISTENING_PORT);
            socket.send(request);
            socket.setSoTimeout(Properties.maxUdpWaitingTime);
            byte[] buffer = new byte[100000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            try{
                socket.receive(reply);
            }catch (SocketTimeoutException ste){
                String error = "6. Sequencer time out";
                frontEnd.log.severe("\nMessage id : " + sequencerId.getId() + "\n"+error+ "\n");
                System.err.println(error);
                returnMessage = "Error : sequencer response timeout";
                return;
            }
            sequencerId = new SequencerId(new String(reply.getData()).trim());
            info = "6. Receive sequencer id" ;
            frontEnd.log.info("\nMessage id : " + sequencerId.getId() + "\nis given to Message:\n"+inboundMessage+"\n\n\n");
            System.out.println(info);
            synchronized (frontEnd.getMapLock()){
                frontEnd.getMessageBook().put(sequencerId.getId(), this);
            }
        } catch (IOException e) {
            e.printStackTrace();
            String error = "6. FE inbound message IO exception";
            frontEnd.log.severe("\nMessage id : " + sequencerId.getId() + "\n"+error+ "\n");
            System.err.println(error);
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

        synchronized (this.rrLock) {
            responseCount = rmResponseList.size();
            if(responseCount > 0) r1 = rmResponseList.get(0);
            if(responseCount > 1) r2 = rmResponseList.get(1);
            if(responseCount > 2) r3 = rmResponseList.get(2);
        }
        String info = "11 "+responseCount + " Message received for processing";
        System.out.println(info);
        frontEnd.log.info(info+ "\n");

        if(responseCount == 1){
            //only one gave response, assume that is the good one for now, check the others
            returnMessage = r1.getResponseMessage();
            /*
            THIS IS MOSTLY USED FOR TESTING USING ONLY ONE RM, THIS WILL ALSO CALL FOR MISSING SERVERS
             */
            alertMistakes(r1);
        }else if(responseCount == 2){
            //2 responses
            if (r1.getResponseMessage().equals(r2.getResponseMessage())) {
                //both match, confident, alert about the last one
                returnMessage = r1.getResponseMessage();
                alertMistakes(r1, r2);
            } else {
                //not equal + 1 not on time, => server recover from the last time
                System.err.println("CIM ERROR 1 : MESSAGES DO NOT MATCH AND ONE MISSING");
            }
        }else if(responseCount == 3){
            //3 responses
            if(r1.getResponseMessage().equals(r2.getResponseMessage())
                    &&r2.getResponseMessage().equals(r3.getResponseMessage())
                    &&r1.getResponseMessage().equals(r3.getResponseMessage())){ //all equal, best case
                returnMessage = r1.getResponseMessage();
                frontEnd.log.info("Message Id : " + sequencerId.getId() +"\n" + returnMessage +"\nAll messages match\n");

            }else if(r1.getResponseMessage().equals(r2.getResponseMessage())){ //1 and 2 match
                returnMessage = r1.getResponseMessage();
                alertMistakes(r1, r2);
                logError(r3);

            }else if(r1.getResponseMessage().equals(r3.getResponseMessage())){ //1 and 3 match
                returnMessage = r3.getResponseMessage();
                alertMistakes(r1, r3);
                logError(r2);

            }else if(r2.getResponseMessage().equals(r3.getResponseMessage())){ //2 and 3 match
                returnMessage = r2.getResponseMessage();
                alertMistakes(r2, r3);
                logError(r1);

            }else{
                //no match in three, server recover from the last time
                //todo assuming the first one is good, but this has been assumed not to be reached
                System.err.println("ALL THREE MESSAGES ARE DIFFERENT");
                //use the fastest response are the good answer
                returnMessage = r1.getResponseMessage();
                alertMistakes(r2, r3);
                logError(r2);
                logError(r3);
            }
        }else{
            String error = "CIM ERROR : NO MESSAGE RECEIVED";
            System.err.println(error);
            frontEnd.log.severe("\nMessage id : " + sequencerId.getId() + "\n"+error+ "\n");
            returnMessage = DEFAULT_ERROR;
        }
    }
    private void logError(RmResponse r){
        String error = "Message Id : " + sequencerId.getId() +"\n" + returnMessage +"\n"
                +r.getInet()+":"+r.getRmPort() + " contains error :\n" + r.getResponseMessage()+"\n";
        frontEnd.log.severe(error+ "\n");
        System.err.println(error);
    }



    /**
     * Alert mistake has been detected
     * ALWAYS send message to the good ones only
     * @param responses the corrected ones !!!!!
     */
    private void alertMistakes(RmResponse ... responses){
        String info ;
        if(Properties.singlePcTest && responses.length==1){
            info = "12 Testing - 1 response received from RM - good";
            System.out.println(info);
            frontEnd.log.info("\nMessage id : " + sequencerId.getId() + "\n"+info+ "\n");
        }else{
            info = "12 SERVER ERROR OCCURRED IN " + (3-responses.length) + " SERVER" + (3-responses.length>1 ? "S" :"");
            frontEnd.log.severe("\nMessage id : " + sequencerId.getId() + "\n"+info+ "\n");
            System.err.println(info);
            new Thread(new FrontEndAlert(responses, sequencerId)).run();
        }
    }

    /**
     * add response to list ONLY
     * process data "go" signal is given by thread run()
     * This method determines whether a response is on time or not ONLY
     * @param rmResponse the response to be saved
     */
    void addRmResponseToInboundMessage(RmResponse rmResponse){
        synchronized (this.rrLock){
            rmResponseList.add(rmResponse);

            if(rmResponseList.size()==0){
                //first insert response
                firstResponseTime = System.currentTimeMillis();
                long diff = firstResponseTime - receiveTime;
                //this is number is determined by magic for now
                //minimum waiting time is set to ensure the waiting time is not too short
                if(diff < Properties.minTimeDiff) diff = Properties.minTimeDiff;
                timeOutTime = firstResponseTime + 2 * diff;
            }
            String info = "10.1/ Number of Messages received FE : "+rmResponseList.size();
            frontEnd.log.info("\nMessage id : " + sequencerId.getId() + "\n"+info+ "\n");
            System.out.println(info);
        }
    }
}