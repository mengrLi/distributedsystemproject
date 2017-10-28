package service.remote_interface;

import domain.Campus;
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

    default List<List<TimeSlot>> deleteRoom(String roomIdentifier, Calendar date, List<TimeSlot> list, String admiID) throws RemoteException {
        throw new NotImplementedException();
    }

    default String bookRoom(Campus campusOfInterest, String roomIdentifier, Calendar date, TimeSlot timeSlot, int id) throws RemoteException {
        throw new NotImplementedException();
    }
    default String switchRoom() throws RemoteException{
        throw new NotImplementedException();
    }

    default Map<String, Integer> getAvailableTimeSlot(Calendar date) throws RemoteException{
        throw new NotImplementedException();
    }

    default Map<String, Room> getAvailableTimeSlot(Calendar date, Campus campus) throws RemoteException{
        throw new NotImplementedException();
    }

    default String cancelBooking(String bookingId, Campus campus, int id) throws RemoteException {
        throw new NotImplementedException();
    }

    default boolean checkIDAdmin(String fullID) throws RemoteException {
        throw new NotImplementedException();
    }
}
