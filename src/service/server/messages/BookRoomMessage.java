package service.server.messages;

import com.google.gson.GsonBuilder;
import domain.Campus;
import domain.TimeSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;

@RequiredArgsConstructor
@Getter
public class BookRoomMessage {
    private final Campus campusOfInterest;
    private final String roomNumber;
    private final Calendar date;
    private final TimeSlot timeSlot;
    private final int id;

    public BookRoomMessage(String jsonMessage) {
        BookRoomMessage message = new GsonBuilder().create().fromJson(jsonMessage, BookRoomMessage.class);
        this.campusOfInterest = message.campusOfInterest;
        this.roomNumber = message.roomNumber;
        this.date = message.date;
        this.timeSlot = message.timeSlot;
        this.id = message.id;
    }

    public String getMessage() {
        return new GsonBuilder().create().toJson(this, BookRoomMessage.class);
    }
}
