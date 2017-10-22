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

    default List<List<TimeSlot>> createRoom(String roomNumber, Calendar date, List<TimeSlot> list, String adminID) throws RemoteException {
        throw new NotImplementedException();
    }

    default List<List<TimeSlot>> deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list, String admiID) throws RemoteException {
        throw new NotImplementedException();
    }

    default String bookRoom(CampusName campusOfInterest, String roomNumber, Calendar date, TimeSlot timeSlot, CampusName campusOfID, int id) throws RemoteException{
        throw new NotImplementedException();
    }
    default String switchRoom() throws RemoteException{
        throw new NotImplementedException();
    }

    default Map<String, Integer> getAvailableTimeSlot(Calendar date) throws RemoteException{
        throw new NotImplementedException();
    }

    default Map<String, Room> getAvailableTimeSlot(Calendar date, CampusName campusName) throws RemoteException{
        throw new NotImplementedException();
    }

    default boolean cancelBooking(String booking, CampusName campusName, int id) throws RemoteException {
        throw new NotImplementedException();
    }

    default boolean checkIDAdmin(String fullID) throws RemoteException {
        throw new NotImplementedException();
    }
}
