package service.rm;

import domain.Campus;
import domain.Lock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import service.domain.InternalRequest;
import service.server.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * upon receive a message front sequencer, get the seqId from it and the method to process
 * send the original request to each server
 */

@RequiredArgsConstructor
public class ReplicaManager implements Runnable{
    @Getter private final String rmName;
    @Getter private final String inet;
    @Getter private final int rmListeningPort;

    @Getter private Server dvlServer;
    @Getter private Server wstServer;
    @Getter private Server kklServer;

    private long nonce = 1;
    private final Lock nonceLock = new Lock();
    private final Map<Long, InternalRequest> seqRequestMap = new HashMap<>();
    private final Lock mapLock = new Lock();
    private int errorCount = 0;
    private final Lock errorCountLock = new Lock();

    public boolean delayTest = false;
    public boolean errorTest = false;
    private int testErrorCounter = 0;
    private int testDelayCounter = 0;

    @Override
    public void run() {
        initReplicaManager();
        initUdpListenPort();
    }


    /**
     * initiate all the servers
     */
    private void initReplicaManager(){
        dvlServer = new Server(Campus.DORVAL, this);
        kklServer = new Server(Campus.KIRKLAND, this);
        wstServer = new Server(Campus.WESTMOUNT, this);

        new Thread(dvlServer).start();
        new Thread(wstServer).start();
        new Thread(kklServer).start();

    }

    public void restartServers(long nonce, String dvlJson, String kklJson, String wstJson){
        synchronized (errorCountLock){
            this.errorCount = 0;
        }
        synchronized (this){
            this.nonce = nonce;
            this.dvlServer.loadData(dvlJson);
            this.kklServer.loadData(kklJson);
            this.wstServer.loadData(wstJson);


            //Server variable in RoomRecord needs to be reset
        }
    }

    /**
     * initiate udp port which listens to sequencer
     */
    private void initUdpListenPort() {
        System.out.println(rmName + " starting udp listening port at " + inet+":"+rmListeningPort);

        new Thread(new ReplicaManagerListener(this, rmListeningPort)).start();

        System.out.println(rmName + " listening udp messages from sequencer");
    }
    public long getNonce(){
        synchronized (this.nonceLock){
            return nonce;
        }
    }
    public void increaseNonce(){
        synchronized ((this.nonceLock)){
            ++nonce;
        }
    }

    public int increaseErrorCount(){
        synchronized (this.errorCountLock){
            return ++errorCount;
        }
    }
    public int getErrorCount(){
        synchronized (this.errorCountLock){
            return errorCount;
        }
    }

    public InternalRequest getInternalMessage(long id) {
        synchronized (this.mapLock) {
            return seqRequestMap.get(id);
        }
    }
    public void putInternalMessage(InternalRequest internalRequest){
        synchronized (this.mapLock){
            seqRequestMap.put(internalRequest.getId(), internalRequest);
        }
    }
    public void saveResponseMessage(long id, String responseMessage){
        synchronized (this.mapLock){
            if(seqRequestMap.get(id).getServerResponse()==null){
                seqRequestMap.get(id).setServerResponse(responseMessage);
            }else{
                //should not reach here.
                System.err.println("Response message existed, new incoming message has been ignored");
            }
        }
    }

    public String getServerResponse(long sequencerId) {
        synchronized (this.mapLock){
            return seqRequestMap.get(sequencerId).getServerResponse();
        }
    }

    public boolean getErrorTest() {

            if(errorTest) {
                if (testErrorCounter == 3) {
                    errorTest = false;
                    testErrorCounter = 0;
                } else {
                    testErrorCounter++;
                }
                System.err.println("ERROR TEST " + errorTest);
                System.err.println("CURRENT ERROR TEST COUNTER IS " + testErrorCounter);
            }
            return errorTest;
    }

    public boolean getDelayTest() {

            if(delayTest) {
                if (testDelayCounter == 3) {
                    delayTest = false;
                    testDelayCounter = 0;
                } else {
                    testDelayCounter++;
                }
                System.err.println("DELAY TEST " + delayTest);
                System.err.println("CURRENT DELAY TEST COUNTER IS " + testDelayCounter);

            }
            return delayTest;

    }
}
