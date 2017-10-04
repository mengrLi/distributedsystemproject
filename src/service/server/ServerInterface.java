package service.server;

import domain.CampusName;
import domain.TimeSlot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;

public interface ServerInterface extends Remote{

    default List<List<TimeSlot>> createRoom(String roomNumber, Calendar date, List<TimeSlot> list) throws RemoteException{
        throw new NotImplementedException();
    }

    default List<List<TimeSlot>> deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list) throws RemoteException{
        throw new NotImplementedException();
    }

    default void bookRoom(CampusName campusName, String roomNumber, Calendar date, TimeSlot timeSlot) throws RemoteException{
        throw new NotImplementedException();
    }

    default void getAvailableTimesSlot(Calendar date) throws RemoteException{
        throw new NotImplementedException();
    }

    default void cancelBooking(String booking) throws RemoteException{
        throw new NotImplementedException();
    }

    default void getServerStatus() throws RemoteException{
        throw new NotImplementedException();
    }

}
