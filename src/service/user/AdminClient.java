package service.user;

import domain.CampusName;
import domain.TimeSlot;

import java.util.Date;
import java.util.List;

public class AdminClient extends Client implements UserInterface{

    AdminClient(CampusName campusName, int id){
        super(campusName, id);
    }


    public boolean createRoom(int roomNumber, Date date, List<TimeSlot> list){

        return false;
    }

    public boolean deleteRoom(int roomNumber, Date date, List<TimeSlot> list){
        return false;
    }
}
