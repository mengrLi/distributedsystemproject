package service.server;

import domain.CampusName;
import domain.Rooms;
import domain.TimeSlot;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class CampusServer extends UnicastRemoteObject implements ServerInterface, Runnable{

    private final CampusName campusName;
    private Map<Date, Rooms> roomRecord;
    private boolean serverStatus = false;
    private Object roomLock = new Object();

    public CampusServer(CampusName name) throws RemoteException{
        super(name.port);
        campusName = name;
        roomRecord = new HashMap<>();
    }

    @Override
    public void createRoom(String roomNumber, Calendar date, List<TimeSlot> list) throws RemoteException{
//        try{
//            DatagramSocket socket = new DatagramSocket(campusName.port);
//
//            byte[] in = new byte[10000];
//
//
//        }catch(SocketException e){
//            e.printStackTrace();
//        }
        byte[] v = new byte[1000];
        String s = String.valueOf(v);
        System.out.println(campusName.name);
        for(TimeSlot slot : list){
            System.out.println(slot.getStartTime().getTime() + " " + slot.getEnd().getTime());
        }
    }

    @Override
    public void deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list) throws RemoteException{
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
            Registry registry = LocateRegistry.createRegistry(campusName.port);
            registry.bind(campusName.serverName, this);
            System.out.println(campusName.name + " server has been started");
        }catch(RemoteException | AlreadyBoundException e){
            e.printStackTrace();
        }
        while(serverStatus){
        }
    }
}
