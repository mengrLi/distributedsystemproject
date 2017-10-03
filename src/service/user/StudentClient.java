package service.user;

import domain.CampusName;
import domain.TimeSlot;

import java.util.Calendar;

public class StudentClient extends Client implements UserInterface{


    StudentClient(CampusName campus, int id){
        super(campus, id);
    }


    public boolean bookRoom(CampusName campusName, String roomNumber, Calendar date, TimeSlot timeSlot){
        return false;
    }

    public String getAvailableTimesSlot(Calendar date){
        return null;
    }

    public String cancelBooking(String booking){
        return null;
    }

}
