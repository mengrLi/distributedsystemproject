package service;

import domain.CampusName;
import domain.TimeSlot;

import java.util.Date;
import java.util.List;

public class AdminClient extends Client{

    public AdminClient(CampusName campusName, int id){
        super(campusName, id);
    }


    boolean createRoom(int roomNumber, Date date, List<TimeSlot> list){

        return false;
    }

    boolean deleteRoom(int roomNumber, Date date, List<TimeSlot> list){
        return false;
    }
}
