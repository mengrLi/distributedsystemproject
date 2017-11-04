package service.server.messages;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;

@RequiredArgsConstructor
@Getter
public class GetTimeSlotCountRequest {
    private final Calendar date;

    public GetTimeSlotCountRequest(String jsonMessage) {
        date = new GsonBuilder().create().fromJson(jsonMessage, GetTimeSlotCountRequest.class).date;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, GetTimeSlotCountRequest.class);
    }
}
