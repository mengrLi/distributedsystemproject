package service.server.requests;

import CampusServerCorba.CampusServerInterface;
import com.google.gson.GsonBuilder;
import domain.Campus;
import domain.TimeSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;

@RequiredArgsConstructor
@Getter
public class BookRoomRequest {
    private final Campus campusOfInterest;
    private final String roomNumber;
    private final Calendar date;
    private final TimeSlot timeSlot;
    private final Campus campusOfId;
    private final int id;

    public String sendRequest(CampusServerInterface campusInterface) {
        return campusInterface.bookRoom(toString());
    }

    public static BookRoomRequest parseRequest(String jsonMessage) {
        return new GsonBuilder().create().fromJson(jsonMessage, BookRoomRequest.class);
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, BookRoomRequest.class);
    }
}
