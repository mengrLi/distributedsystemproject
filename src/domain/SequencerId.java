package domain;

import lombok.Getter;
import lombok.Setter;

public class SequencerId{
    @Getter
    @Setter
    private String id;

    public SequencerId(String id){
        this.id = id;
    }
}
