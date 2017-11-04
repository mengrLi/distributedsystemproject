package user_v2;

import domain.Campus;
import domain.Room;
import domain.TimeSlot;
import service.remote_interface.UserInterface;
import service.server.messages.CheckAdminIdRequest;
import service.server.messages.CreateRoomRequest;
import service.server.messages.DeleteRoomRequest;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@SuppressWarnings("Duplicates")
public class AdminClientV2 extends ClientV2 implements UserInterface{
    private final Logger log;
    public AdminClientV2(Campus campus, int id){
        super(campus, id);
        System.out.println("admin client started with id " + campus.abrev + "a" + id);
        fullID = campus.abrev + "a" + id;
        log = Logger.getLogger(fullID + " " + AdminClientV2.class);
        initLogger();
    }

    private void initLogger() {
        try {
            String dir = "src/client_log/";
            log.setUseParentHandlers(false);
            FileHandler fileHandler = new FileHandler(dir + campus.abrev + "A" + id + ".log", true);
            log.addHandler(fileHandler);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkID(){
        boolean response = new CheckAdminIdRequest(fullID).sendRequest(campusInterface);

        System.out.println("checking ID " + fullID + "from server");
        if (response) log.info(" Administrator " + fullID + " has logged into " + campus.name + " server");
        return response;

    }

    @Override
    public Map<String, Room> getAvailableTimeSlot(Calendar date, Campus campus){
//        try{
//            log.info(fullID + " checking available time slot of " + campus.name + " on " + date.getTime());
//            return connect().getAvailableTimeSlot(date, campus);
//        }catch(RemoteException | NotBoundException e){
//            System.err.println(e.getMessage());
//        }
        return null;
    }

    @Override
    public boolean createRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        StringBuilder builder = new StringBuilder();
        builder.append(" :").append(fullID)
                .append(" create room at ").append(campus.name)
                .append(" for ").append(date.getTime()).append("\n");
        for (TimeSlot slot : list) {
            builder.append(" from ").append(slot.getStartTime().getTime()).append(" to ").append(slot.getEndTime().getTime()).append("\n");
        }

        List<List<TimeSlot>> response = new CreateRoomRequest(roomNumber, date, list, fullID).sendRequest(campusInterface);

        if (response == null) {
            log.info("ILLEGAL ACCESS OF SERVER USING INVALID ADMIN ID");
            return false;
        }
        if (response.get(0).size() == 0) {
            log.info(builder.append(" SUCCEEDED").toString());
            return true;
        } else {
            System.err.println("The following time slot was not successfully created");
            builder.append(" Partially succeeded with the following exception\n");
            for (TimeSlot slot : response.get(0))
                builder.append(" from ")
                        .append(slot.getStartTime().getTime())
                        .append(" to ")
                        .append(slot.getEndTime().getTime())
                        .append("\n");

            log.info(builder.toString());
            return false;
        }
    }

    @Override
    public boolean deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list){

        StringBuilder builder = new StringBuilder();
        builder.append(" Administrator ").append(fullID).append(" delete rooms from ")
                .append(campus.name).append(" server ")
                .append(" on ").append(date.getTime()).append(" for\n");
        for (TimeSlot slot : list) {
            builder.append(" from ").append(slot.getStartTime().getTime()).append(" to ")
                    .append(slot.getEndTime().getTime()).append("\n");
        }

        List<List<TimeSlot>> response = new DeleteRoomRequest(roomNumber, date, list, fullID).sendRequest(campusInterface);
        if (response == null) {
            log.info("ILLEGAL ACCESS OF SERVER USING INVALID ADMIN ID");
            return false;
        }
        if (response.get(0).size() == 0) {
            log.info(builder.append(" SUCCEEDED").toString());
            return true;
        } else {
            System.err.println("The following time slot was not successfully deleted");
            builder.append("Partially succeeded with the following exception");
            for (TimeSlot slot : response.get(0)) {
                builder.append(" from ").append(slot.getStartTime().getTime()).append(" to ").append(slot.getEndTime().getTime()).append("\n");
                System.err.println(slot.toString());
            }
            log.severe(builder.toString());
            return false;
        }
    }

    @Override
    public void run(){

    }
}
