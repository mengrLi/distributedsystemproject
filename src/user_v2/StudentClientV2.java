package user_v2;

import domain.Campus;
import domain.TimeSlot;
import service.server.requests.BookRoomRequest;
import service.server.requests.CancelBookingRequest;
import service.server.requests.GetTimeSlotCountRequest;
import service.server.requests.SwitchRoomRequest;

import java.util.Calendar;
import java.util.Map;

/**
 * Student always connect to his home campus
 */
public class StudentClientV2 extends ClientV2 {
    public StudentClientV2(Campus campus, int id){
        super(campus, id);
        fullID = campus.abrev + "s" + id;
    }

    @Override
    public Map<Campus, Integer> getAvailableTimeSlot(Calendar date) {
        return new GetTimeSlotCountRequest(date).sendRequest(campusInterface);
    }

    @Override
    public String bookRoom(Campus campusOfInterest,
                           String roomNumber,
                           Calendar date,
                           TimeSlot timeSlot,
                           Campus campusOfID,
                           int id) {
        return new BookRoomRequest(
                campusOfInterest,
                roomNumber,
                date,
                timeSlot,
                campusOfID,
                id)
                .sendRequest(campusInterface);
    }


    @Override
    public String cancelBooking(String bookingId) {
        return new CancelBookingRequest(bookingId, campus, id).sendResquest(campusInterface);
    }

    @Override
    public Map<String, String> switchRoom(String bookingID,
                                          int studentID,
                                          Campus campus,
                                          Calendar date,
                                          TimeSlot slot,
                                          String roomIdentifier) {
        return new SwitchRoomRequest(
                bookingID,
                studentID,
                campus,
                date,
                slot,
                roomIdentifier
        ).sendRequest(campusInterface);
    }
}
