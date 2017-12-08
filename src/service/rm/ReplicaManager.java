package service.rm;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.Campus;
import domain.Lock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import service.Properties;
import service.domain.InternalRequest;
import service.server.Server;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
    private Map<Long, InternalRequest> seqRequestMap = new HashMap<>();
    private final Lock mapLock = new Lock();
    private int errorCount = 0;
    private final Lock errorCountLock = new Lock();

    private boolean delayTest = false;
    private boolean errorTest = false;
    private int testErrorCounter = 0;
    private int testDelayCounter = 0;

    public final Logger log = Logger.getLogger(ReplicaManager.class.toString());
    private FileHandler fileHandler;

    @Override
    public void run() {
        initLogger();
        initReplicaManager();
        initUdpListenPort();
    }

    private void initLogger(){
        try {
            String dir = "src/log/replica_manager_log/";
            log.setUseParentHandlers(false);
            fileHandler = new FileHandler(dir + rmName + ".LOG", Properties.appendLog);
            log.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            log.info("\nReplica manager log loaded\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * initiate all the servers
     */
    private void initReplicaManager(){
        dvlServer = new Server(Campus.DORVAL);
        kklServer = new Server(Campus.KIRKLAND);
        wstServer = new Server(Campus.WESTMOUNT);

        new Thread(dvlServer).start();
        log.info("DDL server thread started\n");
        new Thread(wstServer).start();
        log.info("WST server thread started\n");
        new Thread(kklServer).start();
        log.info("KKL server thread started\n");
    }

    void restartServers(long nonce, String dvlJson, String kklJson, String wstJson){
        synchronized (errorCountLock){
            this.errorCount = 0;
        }
        log.severe("ALL SERVERS RESTARTING DUE TO ERROR THRESHOLD REACHED");
        synchronized (this){
            System.out.println("8.8.2 error reset, loading servers");
            this.nonce = nonce;
            this.dvlServer.loadData(dvlJson);
            this.kklServer.loadData(kklJson);
            this.wstServer.loadData(wstJson);
        }
        log.severe("ALL SERVERS RELOADED DUE TO ERROR THRESHOLD REACHED");
    }

    /**
     * initiate udp port which listens to sequencer
     */
    private void initUdpListenPort() {
        String msg = rmName + " starting udp listening port at " + inet+":"+rmListeningPort;
        System.out.println(msg);
        log.info(msg + "\n");

        new Thread(new ReplicaManagerListener(this, rmListeningPort)).start();

        System.out.println(rmName + " listening udp messages from sequencer");
    }
    long getNonce(){
        synchronized (this.nonceLock){
            return nonce;
        }
    }
    void increaseNonce(){
        synchronized ((this.nonceLock)){
            ++nonce;
        }
    }

    void increaseErrorCount(){
        synchronized (this.errorCountLock){
            ++errorCount;
        }
    }
    int getErrorCount(){
        synchronized (this.errorCountLock){
            return errorCount;
        }
    }

    InternalRequest getInternalMessage(long id) {
        synchronized (this.mapLock) {
            return seqRequestMap.get(id);
        }
    }
    void putInternalMessage(InternalRequest internalRequest){
        synchronized (this.mapLock){
            seqRequestMap.put(internalRequest.getId(), internalRequest);
            String info = "8.3 RM message saved to rm seq map";
            System.out.println(info);
            log.info(info+"\n");
        }
    }
    void saveResponseMessage(long id, String responseMessage){
        synchronized (this.mapLock){
            if(seqRequestMap.get(id).getServerResponse()==null){
                seqRequestMap.get(id).setServerResponse(responseMessage);
            }else{
                //should not reach here.
                System.err.println("Response message existed, new incoming message has been ignored");
            }
        }
    }

    String getServerResponse(long sequencerId) {
        InternalRequest request = seqRequestMap.get(sequencerId);
        if(request == null) return "Not Found";
        return request.getServerResponse();
    }

    boolean getErrorTest() {

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

    boolean getDelayTest() {

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

    void loadRequestMap(String mapJson) {
        Type type = new TypeToken<Map<Long, InternalRequest>>(){}.getType();
        Map<Long, InternalRequest> map = new GsonBuilder().create().fromJson(mapJson, type);
        synchronized (this.mapLock){
            this.seqRequestMap = map;
        }
        System.out.println("Request map reloaded");
    }

    String getRecords() {
        Type type = new TypeToken<Map<Long, InternalRequest>>(){}.getType();
        synchronized (this.mapLock){
            return new GsonBuilder().create().toJson(seqRequestMap, type);
        }
    }

    /**
     * Release all the locks on file
     * @throws Throwable idk what it is
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        fileHandler.close();
    }
}
