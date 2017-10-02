package domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Campus extends Thread{
    private CampusName campusName;
    private Map<Date, Rooms> roomRecords;
    private int port;

    public Campus(CampusName campusName){
        roomRecords = new HashMap<>();
        this.campusName = campusName;
        port = campusName.port;
    }


    @Override
    public void run(){
        while(true){

        }
    }


}
