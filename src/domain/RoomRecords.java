package domain;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import service.server.Server;

import java.util.*;

public class RoomRecords{
//    private final Server server;

    @Expose private final Campus campus;
    private Server server;

    @Expose private final Map<Calendar, Map<String, Room>> records;

    public RoomRecords(Server server, Campus campus){
        this.server = server;
        this.campus = campus;
        records = new HashMap<>();
        initRoomsAndTimeSlots();
    }


    public Map<String, Room> getRecordsOfDate(Calendar date){
        System.out.println("Accessing room availability of " + date.getTime() + " in " + campus.name);
        return records.getOrDefault(date, new HashMap<>());
    }

    public Room getRoomRecordOfDate(Calendar date, String roomIdentifier){
        return getRecordsOfDate(date).getOrDefault(roomIdentifier, new Room(roomIdentifier));
    }

    public int getAvailableTimeSlotsCountOfDate(Calendar date){
        int roomCount = 0;
        Map<String, Room> roomMap = getRecordsOfDate(date);
        for (Room room : roomMap.values()) {
            for (TimeSlot slot : room.getTimeSlots()) {
                if (slot.getStudentID() == null) ++roomCount;
            }
        }

//        Map<String, Room> roomOfDate = getRecordsOfDate(date);
//        Iterator<String> iterator = roomOfDate.keySet().iterator();
//        List<String> keyList;
//        while(iterator.hasNext()){
//            keyList = new LinkedList<>();
//            keyList.addRmResponseToInboundMessage(iterator.next());
//            if(iterator.hasNext()) keyList.addRmResponseToInboundMessage(iterator.next());
//            if(iterator.hasNext()) keyList.addRmResponseToInboundMessage(iterator.next());
//
//            RoomCounter roomCounter = new RoomCounter(roomOfDate, keyList);
//            new Thread(roomCounter);
//
//            //TODO verify if this is correct
//            roomCount += roomCounter.getCounter();
//        }
        return roomCount;
    }

    //TODO To be changed to map
    public List<List<TimeSlot>> addTimeSlots(Calendar date, String roomIdentifier, List<TimeSlot> slotsList){
        return modifyRoom(roomIdentifier, date, slotsList, true);
    }

    public List<List<TimeSlot>> removeTimeSlots(Calendar date, String roomIdentifier, List<TimeSlot> slotsList){
        return modifyRoom(roomIdentifier, date, slotsList, false);
    }

    private List<List<TimeSlot>> modifyRoom(String roomIdentifier, Calendar date, List<TimeSlot> list, boolean add){
        List<List<TimeSlot>> ret;

        Map<String, Room> rooms = this.records.getOrDefault(date, new HashMap<>());
        Room room = rooms.getOrDefault(roomIdentifier, new Room(roomIdentifier));

        if (add) ret = room.addTimeSlots(list);
        else ret = room.removeTimeSlots(list, server);
        rooms.put(roomIdentifier, room);
        this.records.put(date, rooms);
        return ret;
    }


    /**
     * Book room and return booking ID, should be locked by server
     *
     * @param bookingInfo
     * @return String booking id
     */
    public String bookRoom(BookingInfo bookingInfo) {
        Map<String, Room> getRoomMap = getRecordsOfDate(bookingInfo.getBookingDate());
        if (getRoomMap.size() == 0) return "Error: Date not found"; //no date found
        Room getRoom = getRoomMap.get(bookingInfo.getRoomName());
        if (getRoom == null) return "Error: Room not found"; //no room found
        List<TimeSlot> getSlots = getRoom.getTimeSlots();
        if (getSlots.size() == 0) return "Error: This room's time slot list is empty"; // no time slot available

        for (TimeSlot slot : getSlots) {
            if (slot.getStartTime().equals(bookingInfo.getBookingStartTime())
                    && slot.getEndTime().equals(bookingInfo.getBookingEndTime())) {
                if (slot.getStudentID() == null) {
                    String bookingID = bookingInfo.encodeBookingID();
                    slot.setStudentID(
                            Campus.getCampus(bookingInfo.getStudentCampusAbrev()),
                            bookingInfo.getStudentID(),
                            bookingID
                    );
                    return bookingID;
                } else return "Error: This room has been booked";
            }
        }
        return "Error: Time slot not found";
    }

    public String cancelBooking(BookingInfo bookingInfo) {
        Calendar calendar = bookingInfo.getBookingDate();
        Map<String, Room> getMap = getRecordsOfDate(calendar);
        if (getMap == null) return "Error: No room found on " + calendar.getTime();
        String roomIdentifier = bookingInfo.getRoomName();
        Room getRoom = getMap.get(roomIdentifier);
        if (getRoom == null) return "Error: Room " + roomIdentifier + " cannot be found on " + calendar.getTime();

        List<TimeSlot> slots = getRoom.getTimeSlots();
        if (slots == null) return "Error: No time slot found for room " + roomIdentifier + " on " + calendar.getTime();


        for (TimeSlot slot : slots) {
            if (slot.getStartTime().equals(bookingInfo.getBookingStartTime())
                    && slot.getEndTime().equals(bookingInfo.getBookingEndTime())) {
                if (slot.getStudentID() == null) {
                    String error = "Error: this time slot has not been booked";
                    System.err.println(error);
                    return error;
                } else {
                    System.err.println("RECORD FOUND in " +
                            campus.name.toUpperCase() +
                            "SERVER for STUDENT " +
                            bookingInfo.getStudentID()
                    );
                    slot.cancelBooking();
                    return "Booking has been cancelled for student "
                            + bookingInfo.getStudentID() + " on "
                            + calendar.getTime() + " at " + campus.name;
                }
            }
        }
        return "Error: No booking record found in " + campus.name +
                "server for student " + bookingInfo.getStudentID() + " on " + calendar.getTime();
    }

    public boolean validateBooking(BookingInfo bookingInfo, String bookingID) {
        Room room = getRoomRecordOfDate(bookingInfo.getBookingDate(), bookingInfo.getRoomName());
        if (room.getTimeSlots().size() == 0) {
            return false;
        }
        for (TimeSlot slot : room.getTimeSlots()) {
            if (slot.getStartTime().equals(bookingInfo.getBookingStartTime())
                    && slot.getEndTime().equals(bookingInfo.getBookingEndTime())) {
                String storedId = slot.getBookingID();
                if (storedId != null && storedId.equals(bookingID)) return true;
            }
        }
        return false;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public int getDateCount() {
        return records.size();
    }

    //TODO there is probably some problems here
    @RequiredArgsConstructor
    private class RoomCounter implements Runnable{
        private final Map<String, Room> rooms;
        private final List<String> keyList;

        @Getter
        private int counter = 0;

        private void countFreeTimeSlots(){
            Room room;
            for(String key : keyList){
                room = rooms.get(key);
                for(TimeSlot slot : room.getTimeSlots())
                    if(slot.getStudentID()==null) ++counter;
            }
        }
        @Override
        public void run(){
            countFreeTimeSlots();
        }
    }

    public void initRoomsAndTimeSlots(){
        int year = 2017;
        int month = Calendar.NOVEMBER;
        int minutes = 119;

        for (int day = 23; day <= 30; ++day) {
            Room room;
            Map<String, Room> rooms;
            String roomName;
            TimeSlot slot;
            List<TimeSlot> slots;
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            rooms = new HashMap<>();

            for(int roomN = 1; roomN < 3; ++roomN){
                roomName = String.valueOf(roomN);
                room = new Room(roomName);
                slots = new LinkedList<>();
                for(int hour = 8; hour < 21; hour += 5){
                    Calendar calendar1, calendar2;
                    calendar1 = (Calendar) calendar.clone();
                    calendar1.add(Calendar.HOUR, hour);

                    calendar2 = (Calendar) calendar1.clone();
                    calendar2.add(Calendar.MINUTE, minutes);

                    slot = new TimeSlot(calendar1, calendar2);
                    slots.add(slot);
                }
                room.addTimeSlots(slots);
                rooms.put(roomName, room);
            }
            records.put(calendar, rooms);
        }
    }
}
