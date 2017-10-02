package domain;

import java.util.ArrayList;
import java.util.List;

public class Rooms{
    int roomNumber;
    List<TimeSlot> timeSlots;

    public Rooms(int roomNumber){
        this.roomNumber = roomNumber;
        timeSlots = new ArrayList<>();
    }

    public void setTimeSlots(int startHour, int startMin, int intervalInMinutes){
    }

    public int getRoomNumber(){
        return roomNumber;
    }

    public List<TimeSlot> getTimeSlots(){
        return timeSlots;
    }
}
