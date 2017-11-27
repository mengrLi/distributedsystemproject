package service.frontend;

import java.util.HashMap;
import java.util.Map;

public class MessageRecords{
    Map<String, ClientInboundMessage> map;



    //check null? should not be null at all when it is called
    public ClientInboundMessage getInboundMessage(String key){
        return map.get(key);
    }

    public MessageRecords(){
        this.map = new HashMap<>();
    }

    public int getCount(String seqId){
        return map.get(seqId).getCount();
    }

    public void put(String key, ClientInboundMessage message){
        map.put(key, message);
    }
}
