package service.server;

import domain.*;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.regex.PatternSyntaxException;

public class CampusServer extends UnicastRemoteObject implements ServerInterface, Runnable{

    private final CampusName campusName;
    private boolean serverStatus = false;

    private Map<Calendar, Map<String, Room>> roomRecord;
    private final Lock roomLock = new Lock();

    private Map<Long, Map<Integer, Integer>> studentBookingRecord;


    public CampusServer(CampusName name) throws RemoteException{
        super(name.port);
        campusName = name;
        roomRecord = new HashMap<>();
        studentBookingRecord = new HashMap<>();
    }

    @Override
    public List<List<TimeSlot>> createRoom(String roomNumber, Calendar date, List<TimeSlot> list) throws RemoteException{
        return modifyRoom(roomNumber, date, list, true);
    }

    @Override
    public List<List<TimeSlot>> deleteRoom(String roomNumber, Calendar date, List<TimeSlot> list) throws RemoteException{
        return modifyRoom(roomNumber, date, list, false);
    }

    private List<List<TimeSlot>> modifyRoom(String roomNumber, Calendar date, List<TimeSlot> list, boolean toAdd){
        List<List<TimeSlot>> ret;
        synchronized(this.roomLock){
            Map<String, Room> getMap = this.roomRecord.getOrDefault(date, new HashMap<>());
            Room room = getMap.getOrDefault(roomNumber, new Room(roomNumber));


            if(toAdd) ret = room.addTimeSlots(list);
            else ret = room.removeTimeSlots(list);
            getMap.put(roomNumber, room);
            this.roomRecord.put(date, getMap);
        }
        printRoomRecord();
        return ret;
    }

    private void printRoomRecord(){
        for(Map.Entry<Calendar, Map<String, Room>> entry : roomRecord.entrySet()){
            System.out.println("\nServer Terminal Output : " + Format.formatDate(entry.getKey()) + ":");
            for(Map.Entry<String, Room> entry1 : entry.getValue().entrySet()){
                System.out.println(entry1.getKey() + ":");
                for(TimeSlot slot : entry1.getValue().getTimeSlots()){
                    System.out.println(Format.formatTime(slot.getStartTime()) + " " + Format.formatTime(slot.getEndTime()));
                }
            }
        }
    }

    /**
     * Booking method
     *
     * @param campusOfInterest the campus where you want to book a room
     * @param roomNumber       room number
     * @param date             Calendar date
     * @param timeSlot         the time slot
     * @param campusOfID       student's original campus code
     * @param id               student id in form of int
     * @return response string
     * @throws RemoteException io exception
     */
    @Override
    public String bookRoom(CampusName campusOfInterest, String roomNumber, Calendar date, TimeSlot timeSlot, CampusName campusOfID, int id) throws RemoteException{
        //connect to the right campus using listening socket port
        if(campusOfInterest.name.equals(campusName.name)){
            //this server is needed, no need to connect to others
            synchronized(roomLock){
                Map<String, Room> getRoomMap = roomRecord.get(date);
                if(getRoomMap == null) return "Date not found"; //no date found
                Room getRoom = getRoomMap.get(roomNumber);
                if(getRoom == null) return "Room not found"; //no room found
                List<TimeSlot> getSlots = getRoom.getTimeSlots();
                if(getSlots.size() == 0) return "This room's time slot list is empty"; // no time slot available

                for(TimeSlot slot : getSlots){
                    if(slot.equals(timeSlot)){
                        if(slot.getStudentID() == null){
                            Calendar temp = (Calendar) date.clone();
                            temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());
                            long beginOfWeek = temp.getTimeInMillis();
                            //check booking record
                            Map<Integer, Integer> getWeek
                                    = studentBookingRecord.getOrDefault(beginOfWeek, new HashMap<>());

                            int getCount = getWeek.getOrDefault(id, 0);//not null
                            if(getCount < 3){ //save to booking record
                                getWeek.put(id, getCount + 1);
                                String bookingID = generateRandomString(campusOfInterest, campusOfID, id, date, roomNumber, timeSlot);
                                slot.setStudentID(campusOfID, id, bookingID);
                                studentBookingRecord.put(beginOfWeek, getWeek);
                                System.err.println("You can book " + (2 - getCount) + " more rooms this week");
                                return bookingID;
                            }else return "Booking limit reached";
                        }else return "This room has been booked";
                    }
                }
                return "Time slot not found";
            }
        }else{
            //remote server is needed
            return "NOT DONE YET";
        }
    }

    /**
     * random string generator
     *
     * @return confirmation booking code
     */
    private String generateRandomString(CampusName destinateCampus, CampusName studentCampusAbrev, int studentID,
                                        Calendar bookingDate, String roomName,
                                        TimeSlot timeSlot){
        return RandomString.encode(destinateCampus.abrev, studentCampusAbrev.abrev,
                studentID, bookingDate, roomName, timeSlot.getStartTime(), timeSlot.getEndTime());
    }

    @Override
    public Map<String, Room> getAvailableTimesSlot(Calendar date) throws RemoteException{
        synchronized(roomLock){
            return roomRecord.get(date);
        }
    }

    @Override
    public boolean cancelBooking(String booking) throws RemoteException{
        try{
            String decode = RandomString.decode(booking);
            if(!decode.contains("-")){
                System.err.println("Invalid booking reference input");
                return false;
            }
            String[] delim = decode.split("-");
            if(delim.length != 8){
                System.err.println("Invalid booking reference input");
                return false;
            }
            String campusAbrev = delim[0];

            String studentCampusAbrev = delim[1];

            int studentID = Integer.parseInt(delim[2]);

            Calendar bookingDate = Calendar.getInstance();
            bookingDate.setTimeInMillis(Long.parseLong(delim[3]));

            String roomName = delim[4];

            Calendar bookingStartTime = Calendar.getInstance();
            bookingStartTime.setTimeInMillis(Long.parseLong(delim[5]));

            Calendar bookingEndTime = Calendar.getInstance();
            bookingEndTime.setTimeInMillis(Long.parseLong(delim[6]));

            System.out.println("Booking campus " + campusAbrev);
            System.out.println("Student ID campus " + studentCampusAbrev);
            System.out.println("Date of reservation " + bookingDate.getTime());
            System.out.println("Room " + roomName);
            System.out.println("Start time " + bookingStartTime.getTime());
            System.out.println("End time" + bookingEndTime.getTime());


            if(campusAbrev.equals(studentCampusAbrev)){ //this is the right server, no need of connection
                boolean r1 = removeBooking(bookingDate, roomName, bookingStartTime, bookingEndTime);
                boolean r2 = removeStudentRecord(bookingDate, studentID);
                return r1 && r2;
            }else{

                //remote

            }
            return false;
            //this happens in the correct server only

        }catch(PatternSyntaxException | NumberFormatException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    private boolean removeStudentRecord(Calendar bookingDate, int id){
        Calendar startOfWeek = (Calendar) bookingDate.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.getFirstDayOfWeek());
        long beginOfWeekMilli = startOfWeek.getTimeInMillis();
        synchronized(roomLock){
            Map<Integer, Integer> record = studentBookingRecord.get(beginOfWeekMilli);
            if(record == null) return false;
            Integer count = record.get(id);
            if(count == null) return false;
            record.put(id, count - 1);
            return true;
        }
    }

    private boolean removeBooking(Calendar bookingDate, String roomName,
                                  Calendar bookingStartTime, Calendar bookingEndTime){

        synchronized(roomLock){
            Map<String, Room> getMap = roomRecord.get(bookingDate);
            if(getMap == null) return false;

            Room getRoom = getMap.get(roomName);
            if(getRoom == null) return false;

            List<TimeSlot> slots = getRoom.getTimeSlots();
            if(slots == null) return false;

            TimeSlot temp = new TimeSlot(bookingStartTime, bookingEndTime);
            for(TimeSlot slot : slots){
                if(slot.equals(temp)){
                    System.out.println("Record found");
                    slot.cancelBooking();
                    return true;
                }
            }
            return false;
        }
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
            Registry registry = LocateRegistry.getRegistry(1099);
            registry.bind(campusName.serverName, this);
            System.out.println(campusName.name + " server has been started");
        }catch(RemoteException | AlreadyBoundException e){
            e.printStackTrace();
        }
    }


    public void initServer(){
        int year = 2017;
        int month = Calendar.OCTOBER;
        int minutes = 119;
        Calendar calendar, calendar1, calendar2;
        Room room;
        for(int day = 1; day < 30; ++day){
            calendar = Calendar.getInstance();
            calendar.set(year, month, day, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Map<String, Room> rooms = new HashMap<>();

            for(int roomN = 1; roomN < 11; ++roomN){
                String roomName = String.valueOf(roomN);
                room = new Room(roomName);
                TimeSlot slot;
                List<TimeSlot> slots = new LinkedList<>();
                for(int hour = 8; hour < 21; hour += 3){
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
            roomRecord.put(calendar, rooms);
        }
        System.out.println(roomRecord.size()+" days added to server");
    }
}
