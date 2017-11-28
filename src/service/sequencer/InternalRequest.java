package service.sequencer;


import com.google.gson.GsonBuilder;
import domain.SequencerId;
import lombok.Getter;

public class InternalRequest{
    @Getter private final String method;
    @Getter private final SequencerId sequencerId;
    @Getter private final String clientResquestJson;

    public InternalRequest(String method, String clientResquestJson){
        this.method = method;
        this.clientResquestJson = clientResquestJson;
        sequencerId = new SequencerId("new");
    }

    public void setSequencerId(String id){
        sequencerId.setId(id);
    }

    @Override
    public String toString(){
        return new GsonBuilder().create().toJson(this, InternalRequest.class);
    }

}
