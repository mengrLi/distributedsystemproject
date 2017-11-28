package service.rm;

import service.domain.InternalRequest;

import java.util.Map;

/**
 * upon receive a message front sequencer, get the seqId from it and the method to process
 * send the original request to each server
 */
public class ReplicaManager {
    private long nonce = 0;
    Map<Long, InternalRequest> seqRequestMap;
}
