package service.domain;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class RmResponse{
    /**
     * Reference to the RM
     */
    private final String inet;
    private final int rmPort;
    private final String sequencerId;
    private final String responseMessage;
    private boolean onTime;
    @Override
    public String toString(){
        return new GsonBuilder().create().toJson(this, RmResponse.class);
    }

}
