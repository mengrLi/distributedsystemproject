package service.domain;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import service.rm.ReplicaManager;

@RequiredArgsConstructor
@Getter
@Setter
public class RmResponse{
    /**
     * Reference to the RM
     */
//    private final ReplicaManager replicaManager;
    private final String sequencerId;
    private final String responseMessage;
    private boolean onTime;
    @Override
    public String toString(){
        return new GsonBuilder().create().toJson(this, RmResponse.class);
    }

}
