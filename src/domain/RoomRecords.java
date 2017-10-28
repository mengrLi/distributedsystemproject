package domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import service.server.Server;

import java.util.*;

public class RoomRecords{
//    private final Server server;

    private final Campus campus;

    private final Map<Calendar, Map<String, Room>> records;

    public RoomRecords(Server server, Campus campus){
//        this.server  =server;
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
        final Lock counterLock = new Lock();
        int roomCount = 0;

        Map<String, Room> roomOfDate = getRecordsOfDate(date);
        Iterator<String> iterator = roomOfDate.keySet().iterator();
        List<String> keyList;
        while(iterator.hasNext()){
            keyList = new LinkedList<>();
            keyList.add(iterator.next());
            if(iterator.hasNext()) keyList.add(iterator.next());
            if(iterator.hasNext()) keyList.add(iterator.next());

            RoomCounter roomCounter = new RoomCounter(roomOfDate, keyList);
            new Thread(roomCounter);

            //TODO verify if this is correct
            synchronized(counterLock){
                roomCount+=roomCounter.getCounter();
            }
        }
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
        else ret = room.removeTimeSlots(list);
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
                            Campus.getCampusName(bookingInfo.getStudentCampusAbrev()),
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
        Map<String, Room> getMap = roomRecord.get(bookingInfo.getBookingDate());
        if (getMap == null) return false;

        Room getRoom = getMap.get(bookingInfo.getRoomName());
        if (getRoom == null) return false;

        List<TimeSlot> slots = getRoom.getTimeSlots();
        if (slots == null) return false;

        for (TimeSlot slot : slots) {
            if (slot.getStartTime().equals(bookingInfo.getBookingStartTime())
                    && slot.getEndTime().equals(bookingInfo.getBookingEndTime())) {
                if (slot.getStudentID() == null) {
                    System.err.println("NO RECORD FOUND ON " + campusName.name.toUpperCase() + "SERVER");
                    return false;
                } else {
                    System.err.println("RECORD FOUND ON " +
                            campusName.name.toUpperCase() +
                            "SERVER for STUDENT " +
                            bookingInfo.getStudentID()
                    );
                    slot.cancelBooking();
                    return true;
                }
            }
        }
        error = "Error: BOOKING CANNOT BE REMOVED FROM LOCAL SERVER";

        return false;

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
        int month = Calendar.OCTOBER;
        int minutes = 119;

        for(int day = 1; day < 30; ++day){
            Room room;
            Map<String, Room> rooms;
            String roomName;
            TimeSlot slot;
            List<TimeSlot> slots;
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            rooms = new HashMap<>();

            for(int roomN = 1; roomN < 11; ++roomN){
                roomName = String.valueOf(roomN);
                room = new Room(roomName);
                slots = new LinkedList<>();
                for(int hour = 8; hour < 21; hour += 3){
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
