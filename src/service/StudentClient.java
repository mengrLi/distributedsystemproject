package service;

import domain.CampusName;
import domain.TimeSlot;

import java.util.Date;

public class StudentClient extends Client{


    public StudentClient(CampusName campus, int id){
        super(campus, id);
    }


    boolean bookRoom(CampusName campusName, int roomNumber, Date date, TimeSlot timeSlot){
        return false;
    }

    String getAvailableTimesSlot(Date date){
        return null;
    }

    String cancelBooking(String booking){
        return null;
    }


    private void writeToFile(){

    }
}
