package service.user;

import domain.CampusName;
import domain.TimeSlot;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;

public class AdminClient extends Client implements UserInterface{

    AdminClient(CampusName campusName, int id){
        super(campusName, id);
    }

    public boolean createRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        try{
            connect().createRoom(roomNumber, date, list);
            return true;
        }catch(RemoteException | NotBoundException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        try{
            connect().deleteRoom(roomNumber, date, list);
            return true;
        }catch(RemoteException | NotBoundException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

}
