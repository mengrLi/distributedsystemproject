package service.server;

import domain.CampusName;
import domain.Room;
import domain.TimeSlot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface ServerInterface extends Remote{

    default List<List<TimeSlot>> createRoom(String roomNumber, Calendar date, List<TimeSlot> list) throws RemoteException{
        throw new NotImplementedException();
    }

    default List<List<TimeSlot>> deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list) throws RemoteException{
        throw new NotImplementedException();
    }

    default String bookRoom(CampusName campusOfInterest, String roomNumber, Calendar date, TimeSlot timeSlot, CampusName campusOfID, int id) throws RemoteException{
        throw new NotImplementedException();
    }

    default Map<String, Map<String, Room>> getAvailableTimesSlot(Calendar date) throws RemoteException {
        throw new NotImplementedException();
    }

    default boolean cancelBooking(String booking) throws RemoteException{
        throw new NotImplementedException();
    }
}
