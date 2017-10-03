package service.server;

import domain.CampusName;
import domain.RoomRecord;
import domain.TimeSlot;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;


public class CampusServer extends UnicastRemoteObject implements ServerInterface, Runnable{

    RoomRecord roomRecord;
    private boolean serverStatus = false;
    private CampusName campusName;

    public CampusServer(CampusName name) throws RemoteException{
        super(name.port);
        campusName = name;
        roomRecord = new RoomRecord();
    }

    @Override
    public boolean createRoom(int roomNumber, Date date, List<TimeSlot> list) throws RemoteException{


        return false;
    }

    @Override
    public boolean deleteRoom(int roomNumber, Date date, List<TimeSlot> list) throws RemoteException{
        return false;
    }

    @Override
    public boolean bookRoom(CampusName campusName, int roomNumber, Date date, TimeSlot timeSlot) throws RemoteException{
        return false;
    }

    @Override
    public String getAvailableTimesSlot(Date date) throws RemoteException{
        return null;
    }

    @Override
    public String cancelBooking(String booking) throws RemoteException{
        return null;
    }

    @Override
    public boolean getServerStatus() throws RemoteException{
        return serverStatus;
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
            registry.bind("CampusServer", this);
        }catch(RemoteException | AlreadyBoundException e){
            e.printStackTrace();
        }
        System.out.println("Server is started");
        while(serverStatus){
        }
    }
}
