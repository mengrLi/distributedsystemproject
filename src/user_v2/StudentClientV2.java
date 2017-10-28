package user_v2;

import domain.Campus;
import domain.Room;
import domain.TimeSlot;
import service.remote_interface.UserInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Map;

/**
 * Student always connect to his home campus
 */
public class StudentClientV2 extends ClientV2 implements UserInterface {
    public StudentClientV2(Campus campus, int id){
        super(campus, id);
        fullID = campus.abrev + "s" + id;
    }

    @Override
    public String bookRoom(Campus campusOfInterest, String roomNumber, Calendar date, TimeSlot timeSlot, Campus campusOfID, int id){
        try {
            return connect().bookRoom(campusOfInterest, roomNumber, date, timeSlot, id);
        } catch (RemoteException | NotBoundException e) {
            System.err.println(e.getMessage());
            return "Student Client Error";
        }
    }

    @Override
    public Map<String, Integer> getAvailableTimeSlot(Calendar date){
        return null;
    }

    @Override
    public Map<String, Room> getAvailableTimeSlot(Calendar date, Campus campus){
        try{
            return connect().getAvailableTimeSlot(date, campus);
        }catch(RemoteException | NotBoundException e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public String cancelBooking(String bookingId) {
        try {
            return connect().cancelBooking(bookingId, this.campus, this.id);
        } catch (RemoteException | NotBoundException e) {
            System.err.println(e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public String switchRoom(String bookingID, String studentID, Campus campus, Calendar date, String roomIdentifier){
        return null;
    }

    @Override
    public void run(){

    }
}
