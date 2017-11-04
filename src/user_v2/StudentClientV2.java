package user_v2;

import domain.Campus;
import domain.Room;
import domain.TimeSlot;
import service.remote_interface.UserInterface;
import service.server.messages.BookRoomRequest;
import service.server.messages.GetTimeSlotCountRequest;
import service.server.responses.GetTimeSlotCountResponse;

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

    //changed to corba
    @Override
    public String bookRoom(Campus campusOfInterest, String roomNumber, Calendar date, TimeSlot timeSlot, Campus campusOfID, int id){
        return campusInterface.bookRoom(new BookRoomRequest(campusOfInterest, roomNumber, date, timeSlot, id).toString());
    }

    @Override
    public Map<Campus, Integer> getAvailableTimeSlot(Calendar date) {
        return new GetTimeSlotCountResponse(
                campusInterface.getAvailableTimeSlotCount(new GetTimeSlotCountRequest(date).toString())).getResposne();
    }

    @Override
    public Map<String, Room> getAvailableTimeSlot(Calendar date, Campus campus){


//        try{
//            return connect().getAvailableTimeSlot(date, campus);
//        }catch(RemoteException | NotBoundException e){
//            System.err.println(e.getMessage());
//            return null;
//        }
        return null;
    }

    @Override
    public String cancelBooking(String bookingId) {
//        try {
//            return connect().cancelBooking(bookingId, this.campus, this.id);
//        } catch (RemoteException | NotBoundException e) {
//            System.err.println(e.getMessage());
//            return "Error: " + e.getMessage();
//        }
        return null;
    }

    @Override
    public Map<String, String> switchRoom(String bookingID, int studentID, Campus campus, Calendar date, TimeSlot slot, String roomIdentifier) {
//        try {
//            return connect().switchRoom(bookingID, campus, roomIdentifier, date, slot, studentID);
//        } catch (RemoteException | NotBoundException e) {
//            e.printStackTrace();
//            Map<String, String> ret = new HashMap<>();
//            ret.put("cancel", "Error: remote interface error");
//            ret.put("book", "Error: remote interface error");
//            return ret;
//        }
        return null;
    }

    @Override
    public void run(){

    }
}
