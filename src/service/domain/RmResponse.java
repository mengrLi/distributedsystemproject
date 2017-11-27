package service.domain;

import service.rm.ReplicaManager;

public class RmResponse{
    /**
     * Reference to the RM
     */
    private ReplicaManager replicaManager;
    private String sequencerId;
    private boolean onTime;
    private String responseMessage;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public void setSequencerId(String sequencerId){
        this.sequencerId = sequencerId;
    }

    public boolean isOnTime(){
        return onTime;
    }

    public void setOnTime(boolean onTime){
        this.onTime = onTime;
    }

    public String getSequencerId(){
        return sequencerId;
    }

    public ReplicaManager getReplicaManager() {
        return replicaManager;
    }

    public void setReplicaManager(ReplicaManager replicaManager) {
        this.replicaManager = replicaManager;
    }
}
