package service.remote_interface;

import domain.Campus;
import domain.Room;
import domain.TimeSlot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface UserInterface{

    default boolean checkID() {
        throw new NotImplementedException();
    }

    default boolean createRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        throw new NotImplementedException();
    }

    default boolean deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        throw new NotImplementedException();
    }

    default String bookRoom(Campus campusOfInterest, String roomNumber, Calendar date, TimeSlot timeSlot, Campus campusOfID, int id){
        throw new NotImplementedException();
    }

    default Map<String, Integer> getAvailableTimeSlot(Calendar date){
        throw new NotImplementedException();
    }

    default Map<String, Room> getAvailableTimeSlot(Calendar date, Campus campus){
        throw new NotImplementedException();
    }

    default String cancelBooking(String bookingId) {
        throw new NotImplementedException();
    }

    default Map<String, String> switchRoom(String bookingID, int studentID, Campus campus, Calendar date, TimeSlot slot, String roomIdentifier) {
        throw new NotImplementedException();
    }
}
