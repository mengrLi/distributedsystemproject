package domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import service.server.Server;

import java.util.*;

public class RoomRecords{
    private final Server server;

    private final Campus campus;

    private final Map<Calendar, Map<String, Room>> records;

    public RoomRecords(Server server, Campus campus){
        this.server  =server;
        this.campus = campus;
        records = new HashMap<>();
        initRoomsAndTimeSlots();
    }


    public Map<String, Room> getRecordsOfDate(Calendar date){
        System.out.println("Accessing room availability of " + date.getTime());
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
        Map<String, Room> rooms;
        Room room;
            rooms = this.records.getOrDefault(date, new HashMap<>());
            room = rooms.getOrDefault(roomIdentifier, new Room(roomIdentifier));

            if(add) ret = room.addTimeSlots(list);
            else ret = room.removeTimeSlots(list);
            rooms.put(roomIdentifier, room);
            this.records.put(date, rooms);

        int success = ret.get(1).size();

        int total = success + ret.get(0).size();

        server.getLogFile().info(success + "/" + total
                    + " Time slot for room "
                    + roomIdentifier + " has been"
                    + (add ? " added " : " removed ")
                    + "on " + date.getTime());
        return ret;
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
    @SuppressWarnings("Duplicates")
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
        synchronized(server.getLogLock()){
            server.getLogFile().info(records.size() + " days added to " + campus.name);
        }
    }
}
