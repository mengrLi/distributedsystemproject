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
            List<List<TimeSlot>> response = connect().createRoom(roomNumber, date, list);
            if(response.get(0).size() == 0){
                return true;
            }else{
                System.err.println("The following time slot was not successfully created");
                for(TimeSlot slot : response.get(0)) System.err.println(slot.toString());
                return false;
            }
        }catch(RemoteException | NotBoundException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        try{
            List<List<TimeSlot>> response = connect().deleteRoom(roomNumber, date, list);
            if(response.get(0).size() == 0){
                return true;
            }else{
                System.err.println("The following time slot was not successfully deleted");
                for(TimeSlot slot : response.get(0)) System.err.println(slot.toString());
                return false;
            }
        }catch(RemoteException | NotBoundException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

}
