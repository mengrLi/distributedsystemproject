package domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class RoomRecord{
    private Map<Date, Rooms> roomRecords;

    public RoomRecord(){
        roomRecords = new HashMap<>();
    }

    public void addRoom(Date date, int roomNumber, List<TimeSlot> timeSlots){
        Rooms room = roomRecords.getOrDefault(date, new Rooms(roomNumber));
    }


}
