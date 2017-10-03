package service.server;

import domain.CampusName;
import domain.TimeSlot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface ServerInterface extends Remote{

    default boolean createRoom(int roomNumber, Date date, List<TimeSlot> list) throws RemoteException{
        throw new NotImplementedException();
    }

    default boolean deleteRoom(int roomNumber, Date date, List<TimeSlot> list) throws RemoteException{
        throw new NotImplementedException();
    }

    default boolean bookRoom(CampusName campusName, int roomNumber, Date date, TimeSlot timeSlot) throws RemoteException{
        throw new NotImplementedException();
    }

    default String getAvailableTimesSlot(Date date) throws RemoteException{
        throw new NotImplementedException();
    }

    default String cancelBooking(String booking) throws RemoteException{
        throw new NotImplementedException();
    }

    default boolean getServerStatus() throws RemoteException{
        throw new NotImplementedException();
    }

}
