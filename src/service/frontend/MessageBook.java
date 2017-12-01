package service.frontend;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessageBook {
    Map<String, ClientInboundMessage> map;



    //check null? should not be null at all when it is called
    public ClientInboundMessage getInboundMessage(String key){
        return map.get(key);
    }

    public MessageBook(){
        this.map = new LinkedHashMap<>();
    }

    public void put(String key, ClientInboundMessage message){
        map.put(key, message);
    }
}
