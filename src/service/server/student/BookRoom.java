package service.server.student;

import domain.BookingInfo;
import domain.Campus;
import domain.TimeSlot;
import lombok.RequiredArgsConstructor;

import java.rmi.RemoteException;
import java.util.Calendar;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class BookRoom{
    private final Campus campus;
    private final String roomNumber;
    private final Calendar date;
    private final TimeSlot slot;
    private final String studentID;

    private BookingInfo bookingInfo;
    private String bookingID;



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
//    public String bookRoom(Campus campusOfInterest, String roomNumber,
//                           Calendar date, TimeSlot timeSlot, Campus campusOfID, int id) throws RemoteException {
//        /* Create a booking ID object */
//
//        BookingInfo bookingInfo = new BookingInfo(
//                campusOfInterest.abrev, campus.abrev,
//                id, date, roomNumber,
//                timeSlot.getStartTime(), timeSlot.getEndTime());
//        bookingInfo.setToBook(true);
//
//        StringBuilder builder = new StringBuilder();
//        builder.append(bookingInfo.toString());
//        /*
//        Step 1 : check if student can book a room at the week indicated, since student always connect to his own
//        campus first.
//        //get the key to the week of interest in milliseconds
//        */
//        Calendar weekKey = (Calendar) bookingInfo.getBookingDate().clone();
//        weekKey.set(Calendar.DAY_OF_WEEK, weekKey.getFirstDayOfWeek());
//        long beginOfWeek = weekKey.getTimeInMillis();
//
//        //check booking record
//        Map<Integer, Integer> getWeek = studentBookingRecord.getOrDefault(beginOfWeek, new HashMap<>());
//        int count = getWeek.getOrDefault(bookingInfo.getStudentID(), 0);
//
//        /*
//        Strp 2: if count is less than 3, book the room
//        if the room is in the same campus as student's account, book directly, else connect and send bookingInfo to book
//         */
//        if (count < 3) {
//            String returnBookingId;
//            String message;
//            /* Same campus */
//            if (bookingInfo.getCampusOfInterestAbrev().equals(campus.abrev))
//                message = bookRoomHelperPrivate(bookingInfo);
//            /* different campus */
//            else
//                message = udpSendBookingRequest(bookingInfo);
//            //  Error message break the booking process, returns the error message
//            if (message.substring(0, 6).equals("Error:")) {
//                log.info(builder.append("\n").append(message).toString());
//                return message;
//            } else {
//                log.info(builder.append("-SUCCESS").toString());
//                returnBookingId = message;
//            }
//
//            /* Update student's booking record in student's account server */
//            getWeek.put(bookingInfo.getStudentID(), count + 1);
//            studentBookingRecord.put(beginOfWeek, getWeek);
//            System.err.println("You can book " + (2 - count) + " more rooms this week");
//            return returnBookingId;
//        } else {
//            String msg = "Error: Booking limit reached";
//            log.info(builder.append("\n").append(msg).toString());
//            return msg;
//        }
//    }
//
//    private String bookRoomHelperPrivate(BookingInfo bookingInfo) {
//        synchronized (roomLock) {
//            Map<String, Room> getRoomMap = roomRecord.get(bookingInfo.getBookingDate());
//            if (getRoomMap == null) return "Error: Date not found"; //no date found
//            Room getRoom = getRoomMap.get(bookingInfo.getRoomName());
//            if (getRoom == null) return "Error: Room not found"; //no room found
//            List<TimeSlot> getSlots = getRoom.getTimeSlots();
//            if (getSlots.size() == 0) return "Error: This room's time slot list is empty"; // no time slot available
//
//            for (TimeSlot slot : getSlots) {
//                if (slot.getStartTime().equals(bookingInfo.getBookingStartTime())
//                        && slot.getEndTime().equals(bookingInfo.getBookingEndTime())) {
//                    if (slot.getStudentID() == null) {
//                        String bookingID = bookingInfo.encodeBookingID();
//                        slot.setStudentID(
//                                Campus.getCampusName(bookingInfo.getStudentCampusAbrev()),
//                                bookingInfo.getStudentID(),
//                                bookingID
//                        );
//                        return bookingID;
//                    } else return "Error: This room has been booked";
//                }
//            }
//            return "Error: Time slot not found";
//        }
//    }
//
//    private String udpSendBookingRequest(BookingInfo bookingInfo) {
//        String bookIDString = bookingInfo.toString();
//        byte[] messageByte = bookIDString.getBytes();
//        int serverPort = determinePort(bookingInfo.getCampusOfInterestAbrev());
//        if (serverPort == -1) return "Error: Invalid campus name"; //should never be reached
//        return udpRequest(messageByte, bookIDString.length(), serverPort);
//    }
}
