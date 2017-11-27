package service.frontend;

import domain.Campus;
import domain.SequencerId;
import lombok.RequiredArgsConstructor;
import service.domain.RmResponse;

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
    private final String inboundMessage;
    private final String method;



    private Campus destinationCampus;
    private SequencerId sequencerId;
    private String returnMessage;

    private final long receiveTime;
    private long firstResponseTime = 0;
    private long timeOutTime = 0;

    public List<RmResponse> rmResponseList;

    public ClientInboundMessage(String inboundMessage, String method){
        this.inboundMessage = inboundMessage;
        this.method = method;
        this.receiveTime = System.currentTimeMillis();

        rmResponseList = new LinkedList<>();
    }

    /**
     * add data
     * @param rmResponse
     */
    public void addRmResponseToInboundMessage(RmResponse rmResponse){
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
        if(System.currentTimeMillis()>=timeOutTime || rmResponseList.size()==3) {
            processReturnData();
        }
    }

    /**
     * overall process
     */
    private void processInboundData(){
        parseInboundMessage();
        sendToSequencer();
    }

    /**
     * determine campus
     */
    private void parseInboundMessage(){

    }

    private void sendToSequencer(){
        //udp call

        //getInboundMessage sequencerId
    }

    public String sendResponse(){
        return returnMessage;
    }

    /**
     * process the data returned by RM
     */
    private void processReturnData(){



        returnMessage = "";
    }
}
