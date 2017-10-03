package service.user;

import domain.CampusName;
import domain.TimeSlot;

import java.util.Date;

public class StudentClient extends Client implements UserInterface{


    StudentClient(CampusName campus, int id){
        super(campus, id);
    }


    public boolean bookRoom(CampusName campusName, int roomNumber, Date date, TimeSlot timeSlot){
        return false;
    }

    public String getAvailableTimesSlot(Date date){
        return null;
    }

    public String cancelBooking(String booking){
        return null;
    }


    private void writeToFile(){

    }
}
