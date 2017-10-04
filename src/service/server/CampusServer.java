package service.server;

import domain.CampusName;
import domain.Format;
import domain.Room;
import domain.TimeSlot;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CampusServer extends UnicastRemoteObject implements ServerInterface, Runnable{

    private final CampusName campusName;
    private boolean serverStatus = false;

    private final Object roomLock = new Object();
    private Map<Calendar, Map<String, Room>> roomRecord;


    public CampusServer(CampusName name) throws RemoteException{
        super(name.port);
        campusName = name;
        roomRecord = new HashMap<>();
    }

    @Override
    public List<List<TimeSlot>> createRoom(String roomNumber, Calendar date, List<TimeSlot> list) throws RemoteException{
        return modifyRoom(roomNumber, date, list, true);
    }

    @Override
    public List<List<TimeSlot>> deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list) throws RemoteException{
        return modifyRoom(roomNumber, date, list, false);
    }

    private List<List<TimeSlot>> modifyRoom(String roomNumber, Calendar date, List<TimeSlot> list, boolean toAdd){
        List<List<TimeSlot>> ret;
        synchronized(this.roomLock){
            Map<String, Room> getMap = this.roomRecord.getOrDefault(date, new HashMap<>());
            Room room = getMap.getOrDefault(roomNumber, new Room(roomNumber));


            if(toAdd) ret = room.addTimeSlots(list);
            else ret = room.removeTimeSlots(list);
            getMap.put(roomNumber, room);
            this.roomRecord.put(date, getMap);
        }
        printRoomRecord();
        return ret;
    }

    private void printRoomRecord(){
        for(Map.Entry<Calendar, Map<String, Room>> entry : roomRecord.entrySet()){
            System.out.println("\nServer Terminal Output : " + Format.formatDate(entry.getKey()) + ":");
            for(Map.Entry<String, Room> entry1 : entry.getValue().entrySet()){
                System.out.println(entry1.getKey() + ":");
                for(TimeSlot slot : entry1.getValue().getTimeSlots()){
                    System.out.println(Format.formatTime(slot.getStartTime()) + " " + Format.formatTime(slot.getEndTime()));
                }
            }
        }
    }

    @Override
    public void bookRoom(CampusName campusName, String roomNumber, Calendar date, TimeSlot timeSlot) throws RemoteException{

    }

    @Override
    public void getAvailableTimesSlot(Calendar date) throws RemoteException{
    }

    @Override
    public void cancelBooking(String booking) throws RemoteException{
    }

    @Override
    public void getServerStatus() throws RemoteException{
    }

    public void turnOffServer() throws RemoteException{
        this.serverStatus = false;
        System.err.println("Turning off " + campusName.name + " server...");
        System.err.println(campusName.name + " server is off!");
    }

    @Override
    public void run(){
        serverStatus = true;
        try{
//            Registry registry = LocateRegistry.createRegistry(campusName.port);
            Registry registry = LocateRegistry.getRegistry(1099);
            registry.bind(campusName.serverName, this);
            System.out.println(campusName.name + " server has been started");
        }catch(RemoteException | AlreadyBoundException e){
            e.printStackTrace();
        }
        while(serverStatus){
        }
    }
}
