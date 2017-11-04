package service.server.responses;

import com.google.gson.GsonBuilder;
import domain.TimeSlot;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DeleteRoomResponse {
    private final List<List<TimeSlot>> slots;

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, DeleteRoomResponse.class);
    }

    public static List<List<TimeSlot>> parseResponse(String responseMessage) {
        return new GsonBuilder().create().fromJson(responseMessage, DeleteRoomResponse.class).slots;
    }
}
