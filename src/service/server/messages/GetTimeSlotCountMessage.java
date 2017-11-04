package service.server.messages;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;

@RequiredArgsConstructor
@Getter
public class GetTimeSlotCountMessage {
    private final Calendar date;

    public GetTimeSlotCountMessage(String jsonMessage) {
        date = new GsonBuilder().create().fromJson(jsonMessage, GetTimeSlotCountMessage.class).date;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, GetTimeSlotCountMessage.class);
    }
}
