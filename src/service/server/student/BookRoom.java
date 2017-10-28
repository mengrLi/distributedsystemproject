package service.server.student;

import domain.BookingInfo;
import domain.Campus;
import domain.Room;
import domain.TimeSlot;
import lombok.RequiredArgsConstructor;
import service.server.Server;
import service.server.UdpRequest;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static domain.Campus.determinePort;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class BookRoom{
    private final Campus campusOfInterest;
    private final Campus studentCampus;
    private final String roomIdentifier;
    private final Calendar date;
    private final TimeSlot timeSlot;
    private final int studentID;
    private final Server server;

    private BookingInfo bookingInfo;
    private String bookingID;

    public BookRoom(BookingInfo bookingInfo, Server server) {
        this.bookingInfo = bookingInfo;
        this.server = server;
        this.campusOfInterest = Campus.getCampusName(bookingInfo.getCampusOfInterestAbrev());
        this.studentCampus = Campus.getCampusName(bookingInfo.getStudentCampusAbrev());
        this.roomIdentifier = bookingInfo.getRoomName();
        this.date = bookingInfo.getBookingDate();
        this.timeSlot = new TimeSlot(bookingInfo.getBookingStartTime(), bookingInfo.getBookingEndTime());
        this.studentID = bookingInfo.getStudentID();
    }

    //TODO book reference is wrong

    public String book() throws RemoteException {
        /* Create a booking ID object */

        bookingInfo = new BookingInfo(
                campusOfInterest.abrev, studentCampus.abrev,
                studentID, date, roomIdentifier,
                timeSlot.getStartTime(), timeSlot.getEndTime());
        bookingInfo.setToBook(true);

        StringBuilder builder = new StringBuilder();
        builder.append(bookingInfo.toString());
        /*
        Step 1 : check if student can book a room at the week indicated, since student always connect to his own
        campusOfInterest first.
        //get the key to the week of interest in milliseconds
        */
        Calendar weekKey = (Calendar) bookingInfo.getBookingDate().clone();
        weekKey.set(Calendar.DAY_OF_WEEK, weekKey.getFirstDayOfWeek());

        //check booking record
        int count = server.getStudentBookingRecords().getStudentWeeklyBookingRecords(weekKey, studentID);
        /*
        Strp 2: if count is less than 3, book the room
        if the room is in the same campusOfInterest as student's account, book directly, else connect and send bookingInfo to book
         */
        if (count < 3) {
            String message;
        /* Same campusOfInterest */
            /* different campusOfInterest */
            message = bookingInfo.getCampusOfInterestAbrev().equals(this.campusOfInterest.abrev)
                    ? bookRoomHelperPrivate(bookingInfo)
                    : udpSendBookingRequest(bookingInfo);
            //  Error message break the booking process, returns the error message
            if (message.substring(0, 6).equals("Error:")) {
                synchronized (server.getLogLock()) {
                    server.getLogFile().info(builder.append("\n").append(message).toString());
                }
                return message;
            } else {
                synchronized (server.getLogLock()) {
                    server.getLogFile().info(builder.append("-SUCCESS").toString());
                }
                bookingID = message;
            }

        /* Update student's booking record in student's account server */
            server.getStudentBookingRecords().modifyStudentBookingRecords(weekKey, studentID, bookingID, true);
            System.err.println("You can book " + (2 - count) + " more rooms this week");
            return bookingID;
        } else {
            String msg = "Error: Booking limit reached";
            synchronized (server.getLogLock()) {
                server.getLogFile().info(builder.append("\n").append(msg).toString());
            }
            return msg;
        }
    }


    private String bookRoomHelperPrivate(BookingInfo bookingInfo) {
        synchronized (server.getRoomLock()) {
            Map<String, Room> getRoomMap = server.getRoomRecords().getRecordsOfDate(bookingInfo.getBookingDate());
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
    }

    private String udpSendBookingRequest(BookingInfo bookingInfo) {
        String bookIDString = bookingInfo.toString();
        int serverPort = determinePort(bookingInfo.getCampusOfInterestAbrev());
        if (serverPort == -1) return "Error: Invalid campusOfInterest name"; //should never be reached
        UdpRequest udpRequest = new UdpRequest(server, bookIDString, campusOfInterest);
        return udpRequest.sendRequest();
    }

}
