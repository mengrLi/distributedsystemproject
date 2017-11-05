package user_v2;

import domain.BookingInfo;
import domain.Campus;
import domain.TimeSlot;
import service.server.requests.BookRoomRequest;
import service.server.requests.CancelBookingRequest;
import service.server.requests.GetTimeSlotCountRequest;
import service.server.requests.SwitchRoomRequest;

import java.util.Calendar;
import java.util.Map;

/**
 * Student always connect to his home CAMPUS
 */
public class StudentClientV2 extends ClientV2 {
    public StudentClientV2(Campus campus, int id){
        super(campus, "s", id);
        System.out.println("Student client started with id " + FULL_ID);
        synchronized (LOG_LOCK) {
            LOG.info("\nStudent " + FULL_ID + " logged in");
        }
    }
    @Override
    public Map<Campus, Integer> getAvailableTimeSlot(Calendar date) {
        synchronized (LOG_LOCK) {
            LOG.info("\n" + FULL_ID + " check available time slots count of all campus on " + date.getTime());
        }
        return new GetTimeSlotCountRequest(date).sendRequest(campusInterface);
    }

    @Override
    public String bookRoom(Campus campusOfInterest,
                           String roomNumber,
                           Calendar date,
                           TimeSlot timeSlot,
                           Campus campusOfID,
                           int id) {
        String response = new BookRoomRequest(
                campusOfInterest,
                roomNumber,
                date,
                timeSlot,
                campusOfID,
                id)
                .sendRequest(campusInterface);
        synchronized (LOG_LOCK) {
            LOG.info("\nStudent " + FULL_ID
                    + "\nbooking room " + roomNumber
                    + "\nfrom " + timeSlot.getStartTime().getTime()
                    + "\nto " + timeSlot.getEndTime().getTime()
                    + "\nin " + campusOfInterest.name
                    + "\n---" + (response.startsWith("Error") ? "FAILED" : "SUCCEEDED"));
        }
        return response;
    }


    @Override
    public String cancelBooking(String bookingId) {
        String response = new CancelBookingRequest(bookingId, CAMPUS, ID).sendResquest(campusInterface);
        BookingInfo info = BookingInfo.decode(bookingId);
        synchronized (LOG_LOCK) {
            LOG.info("\nStudent " + FULL_ID
                    + "\ncanceling room " + info.getRoomName()
                    + "\nfrom " + info.getBookingStartTime().getTime()
                    + "\nto " + info.getBookingEndTime().getTime()
                    + "\nin " + info.getCampusOfInterest().name
                    + "\nusing booking ID : " + bookingId
                    + "\n---" + (response.startsWith("Error") ? "FAILED" : "SUCCEEDED"));
        }
        return response;
    }

    @Override
    public Map<String, String> switchRoom(String bookingID,
                                          int studentID,
                                          Campus campus,
                                          Calendar date,
                                          TimeSlot slot,
                                          String roomIdentifier) {
        Map<String, String> response = new SwitchRoomRequest(
                bookingID,
                studentID,
                campus,
                date,
                slot,
                roomIdentifier
        ).sendRequest(campusInterface);
        BookingInfo info = BookingInfo.decode(bookingID);

        synchronized (LOG_LOCK) {
            LOG.info("\nStudent " + FULL_ID
                    + "\nswitching room from " + info.getRoomName()
                    + "\nbetween " + info.getBookingStartTime().getTime()
                    + "\nand " + info.getBookingEndTime().getTime()
                    + "\nin " + info.getCampusOfInterest().name
                    + "\nusing booking ID : " + bookingID
                    + "\n---" + (response.get("cancel").startsWith("Error") ? "FAILED" : "SUCCEEDED")
                    + "\nto new booking in room " + roomIdentifier
                    + "\nbetween " + slot.getStartTime().getTime()
                    + "\nto " + slot.getEndTime().getTime()
                    + "\nin " + campus.name
                    + "\n---" + (response.get("book").startsWith("Error") ? "FAILED" : "SUCCEEDED")
                    + "\nSWITCHING : "
                    + (response.get("cancel").startsWith("Error") || response.get("book").startsWith("Error")
                    ? "FAILED"
                    : "SUCCEEDED")
            );
        }
        return response;
    }
}
