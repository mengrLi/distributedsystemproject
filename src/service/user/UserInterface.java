package service.user;

import domain.CampusName;
import domain.TimeSlot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Calendar;
import java.util.List;

public interface UserInterface{

    default boolean createRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        throw new NotImplementedException();
    }

    default boolean deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        throw new NotImplementedException();
    }

    default boolean bookRoom(CampusName campusName, String roomNumber, Calendar date, TimeSlot timeSlot){
        throw new NotImplementedException();
    }

    default String getAvailableTimesSlot(Calendar date){
        throw new NotImplementedException();
    }

    default String cancelBooking(String booking){
        throw new NotImplementedException();
    }
}
