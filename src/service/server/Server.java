package service.server;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.*;
import lombok.Getter;
import service.remote_interface.ServerInterface;
import service.server.student.BookRoom;

import java.io.IOException;
import java.lang.reflect.Type;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@SuppressWarnings("Duplicates")
public class Server extends UnicastRemoteObject implements ServerInterface, Runnable{
    @Getter
    private final Campus campus;
    @Getter
    private final Administrators administrators;
    @Getter
    private final RoomRecords roomRecords;
    @Getter
    private final StudentBookingRecords studentBookingRecords;

    private final Logger log;
    @Getter
    private final Lock roomLock = new Lock();
    @Getter
    private final Lock logLock = new Lock();

    public Server(Campus campus) throws RemoteException{
        //TO BE CHANGED FOR CORBA
        super(campus.port);

        this.campus = campus;
        administrators = new Administrators(campus);

        log = Logger.getLogger(campus.abrev+ Server.class.getName());
        initLogger();

        roomRecords = new RoomRecords(this, campus);
        studentBookingRecords = new StudentBookingRecords(this, campus);
    }

    private void initLogger() {
        try {
            String dir = "src/server_log/";
            log.setUseParentHandlers(false);
            FileHandler fileHandler = new FileHandler(dir + campus.abrev + ".log", true);
            log.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            synchronized (this.logLock) {
                log.info(campus.name + " log loaded");
                log.info(campus.name + " has been initialized");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        System.out.println(campus.name + " starting");
        bindRegistry();

        //setup udp listener
        UdpListener listener = new UdpListener(campus, this);
        listener.init();

    }


    private void bindRegistry() {
        try{
            LocateRegistry.getRegistry(1099).bind(campus.serverName, this);
            System.out.println(campus.name + " bind to port 1099 RMI port");
        }catch(RemoteException | AlreadyBoundException e){
            e.printStackTrace();
        }
    }


    @Override
    public List<List<TimeSlot>> createRoom(String roomIdentifier, Calendar date, List<TimeSlot> list, String adminID) throws RemoteException{
        if (administrators.contains(adminID)) return roomRecords.addTimeSlots(date, roomIdentifier, list);
        synchronized (this.logLock) {
            log.info("Invalid admin access trying to create rooms in " + campus.name);
        }
        return null;
    }

    @Override
    public List<List<TimeSlot>> deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list, String adminID) throws RemoteException{
        if (administrators.contains(adminID.toLowerCase())) return roomRecords.removeTimeSlots(date, roomNumber, list);
        synchronized (this.logLock) {
            log.info("Invalid admin access trying to delete rooms in " + campus.name);
        }
        return null;
    }

    @Override
    public String bookRoom(Campus campusOfInterest, String roomIdentifier, Calendar date, TimeSlot timeSlot, Campus campusOfID, int id) throws RemoteException {
        BookRoom bookRoom = new BookRoom(campusOfInterest, campusOfID, roomIdentifier, date, timeSlot, id, this);
        return bookRoom.book();
    }

    @Override
    public String switchRoom() throws RemoteException{
        return null;
    }

    @Override
    public Map<String, Integer> getAvailableTimeSlot(Calendar date) throws RemoteException{
        return null;
    }

    @Override
    public Map<String, Room> getAvailableTimeSlot(Calendar date, Campus campus) throws RemoteException{
        if(campus.equals(this.campus)){
            synchronized(roomLock){
                return roomRecords.getRecordsOfDate(date);
            }
        }else{
            String req = "**getMap-" + date.getTimeInMillis();
            UdpRequest udpRequest = new UdpRequest(this, req, campus);
            String json = udpRequest.sendRequest();
            Type type = new TypeToken<Map<String, Room>>(){
            }.getType();
            return new GsonBuilder().create().fromJson(json, type);
        }
    }

    @Override
    public boolean cancelBooking(String booking, Campus campus, int id) throws RemoteException{
        return false;
    }

    @Override
    public boolean checkIDAdmin(String fullID) throws RemoteException{
        System.out.println("reached with " + fullID);
        return administrators.contains(fullID);
    }

    public Logger getLogFile(){
        return log;
    }
}
