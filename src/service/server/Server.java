package service.server;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.*;
import lombok.Getter;
import service.remote_interface.ServerInterface;

import java.io.IOException;
import java.lang.reflect.Type;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.PatternSyntaxException;

public class Server extends UnicastRemoteObject implements ServerInterface, Runnable{
    @Getter
    private final Campus campus;
    @Getter
    private final Administrators administrators;
    @Getter
    private final RoomRecords roomRecords;
    @Getter
    private final StudentBookingRecords studentBookingRecords;

    private final Logger log;
    @Getter
    private final Lock roomLock = new Lock();
    @Getter
    private final Lock logLock = new Lock();

    public Server(Campus campus) throws RemoteException{
        //TO BE CHANGED FOR CORBA
        super(campus.port);

        this.campus = campus;
        administrators = new Administrators(campus);

        log = Logger.getLogger(campus.abrev+ Server.class.getName());
        initLogger();

        roomRecords = new RoomRecords(this, campus);
        studentBookingRecords = new StudentBookingRecords(this, campus);
    }

    private void initLogger() {
        try {
            String dir = "src/server_log/";
            log.setUseParentHandlers(false);
            FileHandler fileHandler = new FileHandler(dir + campus.abrev + ".log", true);
            log.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            synchronized (this.logLock) {
                log.info(campus.name + " log loaded");
                log.info(campus.name + " has been initialized");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        System.out.println(campus.name + " starting");
        bindRegistry();

        //setup udp listener
        UdpListener listener = new UdpListener(campus, this);
        listener.init();

    }


    private void bindRegistry() {
        try{
            LocateRegistry.getRegistry(1099).bind(campus.serverName, this);
            System.out.println(campus.name + " bind to port 1099 RMI port");
        }catch(RemoteException | AlreadyBoundException e){
            e.printStackTrace();
        }
    }


    @Override
    public List<List<TimeSlot>> createRoom(String roomIdentifier, Calendar date, List<TimeSlot> list, String adminID) throws RemoteException{
        if (administrators.contains(adminID)) {
            List<List<TimeSlot>> ret;
            synchronized (roomLock) {
                ret = roomRecords.addTimeSlots(date, roomIdentifier, list);
            }
            int success = ret.get(1).size();
            int total = success + ret.get(0).size();

            synchronized (this.logLock) {
                log.info(success + "/" + total + " Time slot for room "
                        + roomIdentifier + " has been" + " added on " + date.getTime());
            }
            return ret;
        }
        synchronized (this.logLock) {
            log.info("Invalid admin access trying to create rooms in " + campus.name);
        }
        return null;
    }

    @Override
    public List<List<TimeSlot>> deleteRoom(String roomIdentifier, Calendar date, List<TimeSlot> list, String adminID) throws RemoteException {
        if (administrators.contains(adminID.toLowerCase())) {
            List<List<TimeSlot>> ret;
            synchronized (roomLock) {
                ret = roomRecords.removeTimeSlots(date, roomIdentifier, list);
            }
            int success = ret.get(1).size();
            int total = success + ret.get(0).size();
            synchronized (this.logLock) {
                log.info(success + "/" + total + " Time slot for room "
                        + roomIdentifier + " has been" + " added on " + date.getTime());
            }
            return ret;
        }
        synchronized (this.logLock) {
            log.info("Invalid admin access trying to delete rooms in " + campus.name);
        }
        return null;
    }

    /**
     * Booking method
     *
     * @param campusOfInterest the campus where you want to book a room
     * @param roomIdentifier   room number
     * @param date             Calendar date
     * @param timeSlot         the time slot
     * @param studentId        student id in form of int
     * @return response string Booking ID
     * @throws RemoteException io exception
     */
    @Override
    public String bookRoom(Campus campusOfInterest, String roomIdentifier,
                           Calendar date, TimeSlot timeSlot, int studentId) throws RemoteException {
        /* Create a bookingID object */

        BookingInfo bookingInfo = new BookingInfo(
                campusOfInterest.abrev, campus.abrev,
                studentId, date, roomIdentifier,
                timeSlot.getStartTime(), timeSlot.getEndTime());
        bookingInfo.setToBook(true);

        StringBuilder builder = new StringBuilder();
        builder.append(bookingInfo.toString());
        /*
        Step 1 : check if student can book a room at the week indicated, since student always connect to his own
        campus first.
        //get the key to the week of interest in student record
        */

        synchronized (this.roomLock) {
            //check booking record
            int getWeekCount = studentBookingRecords.getWeeklyBookingRecords(date, studentId);
            System.out.println(getWeekCount);
            /*
            Strp 2: if count is less than 3, book the room
            if the room is in the same campus as student's account, book directly, else connect and send bookingInfo to book
            */
            if (getWeekCount < 3) {
                String bookingId;
                String message;
                /* Same campus */
                if (campusOfInterest.equals(this.campus)) message = roomRecords.bookRoom(bookingInfo);
                /* different campus */
                else {
                    //The destination campus can directly book the remove without checking the number of booking
                    //since it has been checked in the host server of the student
                    String udpMessage = bookingInfo.toString();
                    UdpRequest udpRequest = new UdpRequest(this, udpMessage, campusOfInterest);
                    message = udpRequest.sendRequest();
                }
                //  Error message break the booking process, returns the error message
                if (message.substring(0, 6).equals("Error:")) {
                    log.info(builder.append("\n").append(message).toString());
                    return message;
                } else {
                    log.info(builder.append("-SUCCESS").toString());
                    bookingId = message;
                }
                /* Update student's booking record in student's account server */
                int remainingBookingOfWeek = studentBookingRecords.modifyBookingRecords(date, studentId, bookingId, true);
                StringBuilder builder1 = new StringBuilder();
                builder1.append("You can book ")
                        .append(remainingBookingOfWeek)
                        .append(" more ")
                        .append(remainingBookingOfWeek == 1 ? "room" : "rooms")
                        .append(" this week");
                System.err.println(builder1.toString());
                return remainingBookingOfWeek + "///" + bookingId;
            } else {
                String msg = "Error: Booking limit reached for the week of " + CalendarHelpers.getStartOfWeek(date).getTime();
                log.info(builder.append("\n").append(msg).toString());
                return msg;
            }
        }
    }

    @Override
    public String cancelBooking(String bookingId, Campus campusOfStudent, int studentId) throws RemoteException {
        String error;
        try {
            String msg = this.campus.name + ":cancel booking by : "
                    + campusOfStudent.abrev + "s" + studentId + " using booking id " + bookingId;
            BookingInfo bookingInfo = BookingInfo.decode(bookingId);
            if (bookingInfo == null) {
                error = "Error: Booking info processed unsuccessfully";
                synchronized (this.logLock) {
                    log.info(msg + error);
                }
                System.err.println(error);
                return error;
            } else {
                //check student id
                if (bookingInfo.getStudentID() != studentId ||
                        !bookingInfo.getStudentCampusAbrev().equals(campusOfStudent.abrev)) {
                    error = "Error: STUDENT ID DOES NOT MATCH BOOKING RECORD";
                    synchronized (this.logLock) {
                        log.info(msg + error);
                    }
                    System.err.println(error);
                    return error;
                }
                bookingInfo.setToBook(false);
                Campus destinationCampus = bookingInfo.getCampusOfInterest();
                Campus studentIdCampus = bookingInfo.getCampusOfStudent();
                if (destinationCampus == null || studentIdCampus == null) {
                    //should not be reached normally using a system generated booking id
                    error = "Error: Campus name invalid";
                    return error;
                }
                System.out.println("Booking campus " + destinationCampus.name);
                System.out.println("Student ID campus " + studentIdCampus.name);
                System.out.println("Date of reservation " + bookingInfo.getBookingDate().getTime());
                System.out.println("Room " + bookingInfo.getRoomName());
                System.out.println("Start time " + bookingInfo.getBookingStartTime().getTime());
                System.out.println("End time" + bookingInfo.getBookingEndTime().getTime());

                String result;
                if (this.campus.abrev.equals(bookingInfo.getCampusOfInterestAbrev())) {
                    //booking record is on the student's server

                    synchronized (this.roomLock) {
                        result = roomRecords.cancelBooking(bookingInfo);
                    }
                    if (result.substring(0, 5).equals("Error")) {
                        synchronized (this.logLock) {
                            log.info(msg + result);
                        }
                        return result;
                    }
                } else {
                    //booking record is on a remote server
                    String udpMessage = bookingInfo.toString();
                    UdpRequest udpRequest = new UdpRequest(this, udpMessage, destinationCampus);
                    result = udpRequest.sendRequest();
                    if (result.substring(0, 6).equals("Error")) {
                        error = "Error: BOOKING CANNOT BE REMOVED FROM REMOTE SERVER";
                        synchronized (this.logLock) {
                            log.info(msg + "-" + error + "-Remote Message-" + result);
                        }
                        System.err.println(error);
                        return error + "-Remote Error-" + result;
                    }
                }
                //reaching here means booking has been removed properly
                //Student record is always on the current server
                int remainingBookingOfWeek;
                synchronized (this.roomLock) {
                    remainingBookingOfWeek = studentBookingRecords
                            .modifyBookingRecords(
                                    bookingInfo.getBookingDate(), bookingInfo.getStudentID(), bookingId, false
                            );
                }
                if (remainingBookingOfWeek == 4) {
                    //should not reached normally
                    error = "Error: student record could not be found for the week of "
                            + CalendarHelpers.getStartOfWeek(bookingInfo.getBookingDate()).getTime();
                    synchronized (this.logLock) {
                        log.info(msg + " " + error);
                    }
                    return error;
                } else if (remainingBookingOfWeek == -1) {
                    //should not reached normally
                    error = "Error: student booking ID could not be found for the week of "
                            + CalendarHelpers.getStartOfWeek(bookingInfo.getBookingDate()).getTime();
                    synchronized (this.logLock) {
                        log.info(msg + " " + error);
                    }
                    return error;
                }
                return remainingBookingOfWeek + "////" + result;
            }
        } catch (PatternSyntaxException | NumberFormatException e) {
            System.err.println(e.getMessage());
            synchronized (this.logLock) {
                log.info(e.getMessage());
            }
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Cases:
     * 1. both local - simple
     * 2. one local/one remote
     * 3. both remote
     * both same
     * one each
     * <p>
     * First validate cancel, because booking process first
     * Book first because book might fail. whereas cancel guarantee to work
     *
     * @param bookingID
     * @param campus
     * @param roomIdentifier
     * @param newDate
     * @param timeSlot
     * @param studentID
     * @return
     * @throws RemoteException
     */
    @Override
    public Map<String, String> switchRoom(String bookingID, Campus campus, String roomIdentifier,
                                          Calendar newDate, TimeSlot timeSlot, int studentID) throws RemoteException {
        Map<String, String> ret = new HashMap<>();
        BookingInfo cancelBookingInfo = BookingInfo.decode(bookingID);
        if (cancelBookingInfo != null) {
            cancelBookingInfo.setToBook(false);
            BookingInfo newBookingInfo = new BookingInfo(campus.abrev, cancelBookingInfo.getStudentCampusAbrev(),
                    studentID, newDate, roomIdentifier,
                    timeSlot.getStartTime(), timeSlot.getEndTime());
            newBookingInfo.setToBook(true);
            String errorMsg;
            String bookResponse;
            String cancelResponse = "Error: cancellation is not processed since booking was not successful";
            UdpRequest udpRequest;

            String cancelRequest = cancelBookingInfo.toString();
            String bookRequest = newBookingInfo.toString();
            String udpResponse;
            if (validateCancellation(cancelBookingInfo, bookingID)) {
                if (this.campus.equals(cancelBookingInfo.getCampusOfInterest())) {//cancel in this server
                    if (this.campus.equals(campus)) {//both cancel and book in this server
                        synchronized (this.roomRecords) {
                            System.out.println("booking");
                            bookResponse = roomRecords.bookRoom(newBookingInfo);
                            System.out.println("cancelling");
                            cancelResponse = roomRecords.cancelBooking(cancelBookingInfo);
                        }
                    } else {//cancel in this server , book in other server
                        udpRequest = new UdpRequest(this, bookRequest, campus);
                        udpResponse = udpRequest.sendRequest();
                        bookResponse = udpResponse;
                        boolean status = status(udpResponse);

                        if (status) {
                            synchronized (this.roomRecords) {
                                cancelResponse = roomRecords.cancelBooking(cancelBookingInfo);
                            }
                        }
                    }
                } else {//cancel not at this server
                    boolean status;
                    if (this.campus.equals(campus)) {//cancel not in this server , book in this server
                        synchronized (this.roomRecords) {
                            bookResponse = roomRecords.bookRoom(newBookingInfo);
                        }
                        status = status(bookResponse);

                        if (status) {// booking successful
                            udpRequest = new UdpRequest(this, cancelRequest, cancelBookingInfo.getCampusOfInterest());
                            udpResponse = udpRequest.sendRequest();
                            status = status(udpResponse);
                            if (!status) {/*
                            cancel in remote failed, cancel the booking just made in this server
                            this should not be reached, since cancel should always succeed since the existance of the/
                            booking has been checked at the beginning
                            */
                                synchronized (this.roomLock) {
                                    bookResponse = roomRecords.cancelBooking(BookingInfo.decode(bookResponse));
                                }
                            }//else unneeded
                        }// else booking failed, cancellation is cancelled
                    } else {//both in remote server
                        udpRequest = new UdpRequest(this, bookRequest, campus);
                        bookResponse = udpRequest.sendRequest();
                        status = status(bookResponse);
                        if (status) {
                            udpRequest = new UdpRequest(this, cancelRequest, cancelBookingInfo.getCampusOfInterest());
                            cancelResponse = udpRequest.sendRequest();
                            if (!status(cancelResponse)) {
                            /*cancel in remote failed, cancel the booking just made in this server
                            this should not be reached, since cancel should always succeed since the existance of the/
                            booking has been checked at the beginning
                            */
                                BookingInfo cancel = BookingInfo.decode(bookResponse);
                                cancel.setToBook(false);
                                udpRequest = new UdpRequest(this, cancel.toString(), campus);
                                bookResponse = udpRequest.sendRequest();
                            }
                        }// else booking failed, cancellation is cancelled
                    }
                }
                ret.put("cancel", cancelResponse);
                ret.put("book", bookResponse);

                //change the booking accordingly
                Calendar oldStartOfWeek, newStartOfWeek;
                oldStartOfWeek = CalendarHelpers.getStartOfWeek(cancelBookingInfo.getBookingDate());
                newStartOfWeek = CalendarHelpers.getStartOfWeek(newDate);
                if (!oldStartOfWeek.equals(newStartOfWeek)) {
                    synchronized (this.roomLock) {
                        studentBookingRecords.modifyBookingRecords(cancelBookingInfo.getBookingDate(), studentID, bookingID, false);
                        studentBookingRecords.modifyBookingRecords(newDate, studentID, bookingID, true);
                    }
                }
                return ret;
            } else {
                errorMsg = "Error: BookingID cannot be found at " + cancelBookingInfo.getCampusOfInterest()
                        + "Switch was not performed";
                synchronized (this.logLock) {
                    log.info(errorMsg);
                }
                ret.put("cancel", cancelResponse);
                ret.put("book", errorMsg);
                return ret;
            }
        } else {
            String errorMsg = "Error: BookingID invalid! Switch was not performed";
            synchronized (this.logLock) {
                log.info("Booking ID is invalid");
            }
            ret.put("cancel", "Error: cancellation is not processed since booking was not successful");
            ret.put("book", errorMsg);
            return ret;
        }

    }

    private boolean status(String udpResponse) {
        return !udpResponse.substring(0, 5).equals("Error");
    }

    private boolean validateCancellation(BookingInfo cancelBookingInfo, String bookingID) {
        if (this.campus.equals(cancelBookingInfo.getCampusOfInterest())) {
            synchronized (this.roomLock) {
                return roomRecords.validateBooking(cancelBookingInfo, bookingID);
            }
        } else {
            String msg = "**chkCnl" + bookingID;
            UdpRequest request = new UdpRequest(this, msg, cancelBookingInfo.getCampusOfInterest());
            return Boolean.parseBoolean(request.sendRequest());
        }
    }

    @Override
    public Map<String, Integer> getAvailableTimeSlot(Calendar date) throws RemoteException{
        return null;
    }

    @Override
    public Map<String, Room> getAvailableTimeSlot(Calendar date, Campus campus) throws RemoteException{
        if(campus.equals(this.campus)){
            synchronized(roomLock){
                return roomRecords.getRecordsOfDate(date);
            }
        }else{
            String req = "**getMap-" + date.getTimeInMillis();
            UdpRequest udpRequest = new UdpRequest(this, req, campus);
            String json = udpRequest.sendRequest();
            Type type = new TypeToken<Map<String, Room>>(){
            }.getType();
            return new GsonBuilder().create().fromJson(json, type);
        }
    }

    @Override
    public boolean checkIDAdmin(String fullID) throws RemoteException{
        System.out.println("reached with " + fullID);
        return administrators.contains(fullID);
    }
    public Logger getLogFile(){
        return log;
    }

}
