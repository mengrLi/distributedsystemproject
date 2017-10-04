package service.user;

import domain.CampusName;
import domain.Room;
import domain.TimeSlot;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Map;

public class StudentClient extends Client implements UserInterface{


    StudentClient(CampusName campus, int id){
        super(campus, id);
    }


    public String bookRoom(CampusName campusOfInterest, String roomNumber, Calendar date, TimeSlot timeSlot, CampusName campusOfID, int id){
        try{
            return connect().bookRoom(campusOfInterest, roomNumber, date, timeSlot, campusOfID, id);
        }catch(RemoteException | NotBoundException e){
            System.err.println(e.getMessage());
            return "error";
        }
    }

    public Map<String, Room> getAvailableTimesSlot(Calendar date){
        try{
            return connect().getAvailableTimesSlot(date);
        }catch(RemoteException | NotBoundException e){
            System.err.println(e.getMessage());
        }
        return null;
    }

    public boolean cancelBooking(String booking){
        try{
            return connect().cancelBooking(booking);
        }catch(RemoteException | NotBoundException e){
            e.printStackTrace();
        }
        return false;
    }

}
