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
    public long increaseNonce(){
        synchronized ((this.nonceLock)){
            return ++nonce;
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
}
