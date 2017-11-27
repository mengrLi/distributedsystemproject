package service.domain;

public class RmResponse{
    private String sequencerId;
    private boolean onTime;

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
}
