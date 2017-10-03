package service.user;

import domain.CampusName;
import domain.TimeSlot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Date;
import java.util.List;

public interface UserInterface{

    default boolean createRoom(int roomNumber, Date date, List<TimeSlot> list){
        throw new NotImplementedException();
    }

    default boolean deleteRoom(int roomNumber, Date date, List<TimeSlot> list){
        throw new NotImplementedException();
    }

    default boolean bookRoom(CampusName campusName, int roomNumber, Date date, TimeSlot timeSlot){
        throw new NotImplementedException();
    }

    default String getAvailableTimesSlot(Date date){
        throw new NotImplementedException();
    }

    default String cancelBooking(String booking){
        throw new NotImplementedException();
    }
}
