package service.server.messages;

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
    private final int id;

    public String getMessage() {
        return new GsonBuilder().create().toJson(this, BookRoomRequest.class);
    }


    public static BookRoomRequest parseMessage(String jsonMessage) {
        return new GsonBuilder().create().fromJson(jsonMessage, BookRoomRequest.class);
    }
}
