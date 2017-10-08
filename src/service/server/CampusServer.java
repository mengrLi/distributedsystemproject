package service.server;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.PatternSyntaxException;

public class CampusServer extends UnicastRemoteObject implements ServerInterface, Runnable{

    private final Lock roomLock = new Lock();
    private final CampusName campusName;
    private Map<Calendar, Map<String, Room>> roomRecord;
    private Map<Long, Map<Integer, Integer>> studentBookingRecord;

    private Logger log = null;

    public CampusServer(CampusName name) throws RemoteException{
        super(name.port);
        campusName = name;
        roomRecord = new HashMap<>();
        studentBookingRecord = new HashMap<>();

        initLogger();
        log.info(campusName.name + " Server has been loaded");

    }

    private void initLogger() {
        try {
            System.out.println("loading log");
            String dir = "src/server_log/";
            log = Logger.getLogger(CampusServer.class.getName());
            log.setUseParentHandlers(false);
            FileHandler fileHandler = new FileHandler(dir + campusName.abrev + ".log", true);
            log.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            System.out.println("loaded log");
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        return ret;
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
     * @return response string Booking ID
     * @throws RemoteException io exception
     */
    @Override
    public String bookRoom(CampusName campusOfInterest, String roomNumber,
                           Calendar date, TimeSlot timeSlot, CampusName campusOfID, int id) throws RemoteException {
        /* Create a booking ID object */

        BookingInfo bookingInfo = new BookingInfo(
                campusOfInterest.abrev, campusName.abrev,
                id, date, roomNumber,
                timeSlot.getStartTime(), timeSlot.getEndTime());
        bookingInfo.setToBook(true);
        /*
        Step 1 : check if student can book a room at the week indicated, since student always connect to his own
        campus first.
        //get the key to the week of interest in milliseconds
        */
        Calendar weekKey = (Calendar) bookingInfo.getBookingDate().clone();
        weekKey.set(Calendar.DAY_OF_WEEK, weekKey.getFirstDayOfWeek());
        long beginOfWeek = weekKey.getTimeInMillis();

        //check booking record
        Map<Integer, Integer> getWeek = studentBookingRecord.getOrDefault(beginOfWeek, new HashMap<>());
        int count = getWeek.getOrDefault(bookingInfo.getStudentID(), 0);

        /*
        Strp 2: if count is less than 3, book the room
        if the room is in the same campus as student's account, book directly, else connect and send bookingInfo to book
         */
        if (count < 3) {
            String returnBookingId;
            String message;
            /* Same campus */
            if (bookingInfo.getCampusOfInterestAbrev().equals(campusName.abrev))
                message = bookRoomHelperPrivate(bookingInfo);
            /* different campus */
            else
                message = udpSendBookingRequest(bookingInfo);

            //  Error message break the booking process, returns the error message
            if (message.substring(0, 6).equals("Error:")) return message;
            else returnBookingId = message;

            /* Update student's booking record in student's account server */
            getWeek.put(bookingInfo.getStudentID(), count + 1);
            studentBookingRecord.put(beginOfWeek, getWeek);
            System.err.println("You can book " + (2 - count) + " more rooms this week");
            return returnBookingId;
        } else return "Error: Booking limit reached";
    }

    private String bookRoomHelperPrivate(BookingInfo bookingInfo) {
        synchronized (roomLock) {
            Map<String, Room> getRoomMap = roomRecord.get(bookingInfo.getBookingDate());
            if (getRoomMap == null) return "Error: Date not found"; //no date found
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
                                CampusName.getCampusName(bookingInfo.getStudentCampusAbrev()),
                                bookingInfo.getStudentID(),
                                bookingID
                        );
                        return bookingID;
                    } else return "Error: This room has been booked";
                }
            }
            return "Error: Time slot not found";
        }
    }

    private String udpSendBookingRequest(BookingInfo bookingInfo) {
        String bookIDString = bookingInfo.toString();
        byte[] messageByte = bookIDString.getBytes();
        int serverPort = determinePort(bookingInfo.getCampusOfInterestAbrev());
        if (serverPort == -1) return "Error: Invalid campus name"; //should never be reached
        return udpRequest(messageByte, bookIDString.length(), serverPort);
    }

    private int determinePort(String campusOfInterestAbrev) {
        CampusName ret = CampusName.getCampusName(campusOfInterestAbrev);
        if (ret != null) return ret.inPort;
        System.err.println("Campus invalid");
        return -1;
    }


    /**
     * UDP request helper
     * @param messageInByte message string in byte array
     * @param length length of the original message
     * @param serverPort port which is listening
     * @return string response from the other server
     */
    private String udpRequest(byte[] messageInByte, int length, int serverPort) {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(messageInByte, length, address, serverPort);
            socket.send(request);
            byte[] buffer = new byte[100000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
            return new String(reply.getData()).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error: I/O Exception";
    }




    @Override
    public Map<String, Integer> getAvailableTimeSlot(Calendar date) throws RemoteException{
        Map<String, Integer> ret = new HashMap<>();
        for(CampusName name : CampusName.values()) ret.put(name.abrev, checkFreeRooms(date, name));
        return ret;
    }

    private int checkFreeRooms(Calendar date, CampusName name){
        if(name.name.equals(this.campusName.name)){
            return countFreeRooms(date);
        }else{
            int port = determinePort(name.abrev);
            String req = "**countA-" + date.getTimeInMillis();
            String response = udpRequest(req.getBytes(), req.length(), port);
            try{
                return Integer.parseInt(response);
            }catch(NumberFormatException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
                return -1;
            }
        }
    }

    private int countFreeRooms(Calendar calendar){
        synchronized(roomLock){
            Map<String, Room> getDate = roomRecord.get(calendar);
            int counter = 0;
            if (getDate == null) {
                return 0;
            }
            for(Map.Entry<String, Room> entry : getDate.entrySet())
                for(TimeSlot slot : entry.getValue().getTimeSlots())
                    if(slot.getStudentID() == null) ++counter;
            return counter;
        }
    }

    @Override
    public Map<String, Room> getAvailableTimeSlot(Calendar date, CampusName campusName) throws RemoteException{
        if(campusName.equals(this.campusName)){
            synchronized(roomLock){
                return roomRecord.get(date);
            }
        }else{
            int port = campusName.inPort;
            String req = "**countB-" + date.getTimeInMillis();
            String json = udpRequest(req.getBytes(), req.length(), port).trim();
            Type type = new TypeToken<Map<String, Room>>(){
            }.getType();
            return new GsonBuilder().create().fromJson(json, type);
        }
    }


    /**
     * When canceling, student connects to his own campus
     *
     * @param bookingID
     * @param campusName
     * @param id
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean cancelBooking(String bookingID, CampusName campusName, int id) throws RemoteException {
        try {
            BookingInfo bookingInfo = BookingInfo.decode(bookingID);
            if (bookingInfo == null) {
                System.err.println("Booking info process unsuccessful");
                return false;
            } else {
                if (bookingInfo.getStudentID() != id || !bookingInfo.getStudentCampusAbrev().equals(campusName.abrev)) {
                    System.err.println("STUDENT ID DOES NOT MATCH BOOKING RECORD");
                    return false;
                }
                bookingInfo.setToBook(false);
                System.out.println("Booking campus " + bookingInfo.getCampusOfInterestAbrev());
                System.out.println("Student ID campus " + bookingInfo.getStudentCampusAbrev());
                System.out.println("Date of reservation " + bookingInfo.getBookingDate().getTime());
                System.out.println("Room " + bookingInfo.getRoomName());
                System.out.println("Start time " + bookingInfo.getBookingStartTime().getTime());
                System.out.println("End time" + bookingInfo.getBookingEndTime().getTime());


                if (this.campusName.abrev.equals(bookingInfo.getCampusOfInterestAbrev())) {
                    //booking record is on the student's server
                    boolean result = removeBookingRecord(bookingInfo);
                    if (!result) {
                        System.err.println("BOOKING CANNOT BE REMOVED FROM LOCAL SERVER");
                        return false;
                    }
                } else {
                    //booking record is on a remote server
                    boolean result = Boolean.parseBoolean(udpSendBookingRequest(bookingInfo));
                    if (!result) {
                        System.err.println("BOOKING CANNOT BE REMOVED FROM REMOTE SERVER");
                        return false;
                    }
                }
                //reaching here means booking has been removed properly
                //Student record is always on the current server
                return removeStudentRecord(bookingInfo);
            }
        }catch(PatternSyntaxException | NumberFormatException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    private boolean removeBookingRecord(BookingInfo bookingInfo) {

        synchronized(roomLock){
            Map<String, Room> getMap = roomRecord.get(bookingInfo.getBookingDate());
            if(getMap == null) return false;

            Room getRoom = getMap.get(bookingInfo.getRoomName());
            if(getRoom == null) return false;

            List<TimeSlot> slots = getRoom.getTimeSlots();
            if(slots == null) return false;

            for(TimeSlot slot : slots){
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
            return false;
        }
    }

    private boolean removeStudentRecord(BookingInfo bookingInfo) {
        Calendar startOfWeek = (Calendar) bookingInfo.getBookingDate().clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.getFirstDayOfWeek());
        long beginOfWeekMilli = startOfWeek.getTimeInMillis();
        synchronized (roomLock) {
            Map<Integer, Integer> record = studentBookingRecord.get(beginOfWeekMilli);
            int id = bookingInfo.getStudentID();
            if (record == null) return false;
            Integer count = record.get(id);
            if (count == null) return false;
            record.put(id, count - 1);
            return true;
        }
    }
    @Override
    public void run() {
        bindRegistry();
        udpListening();
    }

    private void bindRegistry() {
        try{
            Registry registry = LocateRegistry.getRegistry(1099);
            registry.bind(campusName.serverName, this);
            System.out.println(campusName.name + " server has been started");
        }catch(RemoteException | AlreadyBoundException e){
            e.printStackTrace();
        }
    }

    private void udpListening() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(campusName.inPort);
            byte[] buffer;
            DatagramPacket request;
            while (true) {
                buffer = new byte[100000];
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                new Thread(new Responder(socket, request)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
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

    public class Responder implements Runnable {
        private DatagramSocket socket = null;
        private DatagramPacket request = null;

        Responder(DatagramSocket socket, DatagramPacket packet) {
            this.socket = socket;
            this.request = packet;
        }

        @Override
        public void run() {
            try {
                byte[] data = makeResponse();
                if(data != null){
                    DatagramPacket response = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
                    socket.send(response);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }

        private byte[] makeResponse() {
            BookingInfo bookingInfo;
            String json = new String(request.getData()).trim();
            String requestType = json.substring(2, 8);
            switch (requestType) {
                case "toBook":
                    bookingInfo = new GsonBuilder().create().fromJson(json, BookingInfo.class);
                    if (bookingInfo.isToBook()) {
                        //to book
                        return bookRoomHelperPrivate(bookingInfo).getBytes();
                    } else {
                        //to cancel
                        return String.valueOf(removeBookingRecord(bookingInfo)).getBytes();
                    }
                case "countA": {
                    String[] delim = json.split("-");
                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTimeInMillis(Long.parseLong(delim[1]));
                        int get = countFreeRooms(calendar);
                        return String.valueOf(get).getBytes();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                case "countB": {
                    String[] delim = json.split("-");
                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTimeInMillis(Long.parseLong(delim[1]));
                        Type type = new TypeToken<Map<String, Room>>() {
                        }.getType();
                        synchronized (roomLock) {
                            Map<String, Room> ret = roomRecord.get(calendar);
                            return new GsonBuilder().create().toJson(ret, type).getBytes();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                case "remove": {
                    String[] delim = json.split("-");
                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTimeInMillis(Long.parseLong(delim[1]));
                        int id = Integer.parseInt(delim[2]);
                        synchronized (roomLock) {
                            int count = studentBookingRecord.get(calendar.getTimeInMillis()).get(id);
                            studentBookingRecord.get(calendar.getTimeInMillis()).put(id, count - 1);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            return null;
        }
    }
}
