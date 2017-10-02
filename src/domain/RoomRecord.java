package domain;

import java.util.Date;
import java.util.List;

public class RoomRecord{
    String recordID;
    Date date;
    int roomNumber;
    List<TimeSlot> availability;
    long bookedBy;


}
