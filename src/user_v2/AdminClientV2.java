package user_v2;

import domain.Campus;
import domain.TimeSlot;
import service.server.requests.CheckAdminIdRequest;
import service.server.requests.CreateRoomRequest;
import service.server.requests.DeleteRoomRequest;

import java.util.Calendar;
import java.util.List;

@SuppressWarnings("Duplicates")
public class AdminClientV2 extends ClientV2 {

    public AdminClientV2(Campus campus, int id){
        super(campus, "a", id);
        System.out.println("Admin client started with id " + FULL_ID);
        synchronized (this.LOG_LOCK) {
            LOG.info("Admin " + FULL_ID + " logged in");
        }
    }
    @Override
    public boolean checkID(){
        boolean response = new CheckAdminIdRequest(FULL_ID).sendRequest(campusInterface);
        System.out.println("checking ID " + FULL_ID + "from server");
        synchronized (this.LOG_LOCK) {
            if (response) LOG.info(" Administrator " + FULL_ID + " has logged into " + CAMPUS.name + " server");
            else LOG.info(FULL_ID + "is trying to access " + CAMPUS.name + ": Access DENIED");
        }
        return response;
    }

    @Override
    public boolean createRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        StringBuilder builder = new StringBuilder();
        builder.append(" :").append(FULL_ID)
                .append(" create room at ").append(CAMPUS.name)
                .append(" for ").append(date.getTime()).append("\n");
        for (TimeSlot slot : list)
            builder.append(" from ")
                    .append(slot.getStartTime().getTime()).append(" to ")
                    .append(slot.getEndTime().getTime()).append("\n");
        List<List<TimeSlot>> response = new CreateRoomRequest(roomNumber, date, list, FULL_ID)
                .sendRequest(campusInterface);
        if (response == null) {
            synchronized (this.LOG_LOCK) {
                LOG.info("ILLEGAL ACCESS OF SERVER USING INVALID ADMIN ID");
            }
            return false;
        }
        if (response.get(0).size() == 0) {
            synchronized (this.LOG_LOCK) {
                LOG.info(builder.append(" SUCCEEDED").toString());
            }
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
            synchronized (this.LOG_LOCK) {
                LOG.info(builder.toString());
            }
            return false;
        }
    }
    @Override
    public boolean deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list){
        StringBuilder builder = new StringBuilder();
        builder.append(" Administrator ").append(FULL_ID).append(" delete rooms from ")
                .append(CAMPUS.name).append(" server ")
                .append(" on ").append(date.getTime()).append(" for\n");
        for (TimeSlot slot : list)
            builder.append(" from ").append(slot.getStartTime().getTime()).append(" to ")
                    .append(slot.getEndTime().getTime()).append("\n");
        List<List<TimeSlot>> response = new DeleteRoomRequest(roomNumber, date, list, FULL_ID)
                .sendRequest(campusInterface);
        if (response == null) {
            synchronized (LOG_LOCK) {
                LOG.info("ILLEGAL ACCESS OF SERVER USING INVALID ADMIN ID");
            }
            return false;
        }
        if (response.get(0).size() == 0) {
            synchronized (LOG_LOCK) {
                LOG.info(builder.append(" SUCCEEDED").toString());
            }
            return true;
        } else {
            System.err.println("The following time slot was not successfully deleted");
            builder.append("Partially succeeded with the following exception");
            for (TimeSlot slot : response.get(0)) {
                builder.append(" from ").append(slot.getStartTime().getTime()).append(" to ")
                        .append(slot.getEndTime().getTime()).append("\n");
                System.err.println(slot.toString());
            }
            synchronized (LOG_LOCK) {
                LOG.severe(builder.toString());
            }
            return false;
        }
    }

}