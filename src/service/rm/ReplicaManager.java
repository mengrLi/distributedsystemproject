package service.rm;

import domain.Campus;
import lombok.RequiredArgsConstructor;
import service.domain.InternalRequest;
import service.server.Server;

import javax.print.attribute.standard.PrinterURI;
import java.util.HashMap;
import java.util.Map;

/**
 * upon receive a message front sequencer, get the seqId from it and the method to process
 * send the original request to each server
 */

@RequiredArgsConstructor
public class ReplicaManager implements Runnable{
    private final String rmName;
    private final String inet;
    private final int rmListeningPort;

    private Server dvlServer;
    private Server wstServer;
    private Server kklServer;

    private long nonce = 1;
    private final Map<Long, InternalRequest> seqRequestMap = new HashMap<>();

    private int errorCount = 0;

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

    /**
     * initiate udp port which listens to sequencer
     */
    private void initUdpListenPort() {
        System.out.println(rmName + " starting udp listening port at " + inet+":"+rmListeningPort);

        new Thread(new ReplicaManagerListener(this, rmListeningPort)).start();

        System.out.println(rmName + " listening udp messages from sequencer");
    }
}
