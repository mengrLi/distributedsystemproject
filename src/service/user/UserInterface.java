package service.user;

import domain.CampusName;
import domain.Room;
import domain.TimeSlot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface UserInterface{

    default boolean createRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        throw new NotImplementedException();
    }

    default boolean deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        throw new NotImplementedException();
    }

    default String bookRoom(CampusName campusOfInterest, String roomNumber, Calendar date, TimeSlot timeSlot, CampusName campusOfID, int id){
        throw new NotImplementedException();
    }

    default Map<String, Room> getAvailableTimesSlot(Calendar date){
        throw new NotImplementedException();
    }

    default boolean cancelBooking(String booking){
        throw new NotImplementedException();
    }
}
