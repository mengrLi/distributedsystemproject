package service.server.responses;

import com.google.gson.GsonBuilder;
import domain.Room;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GetTimeSlotByRoomResponse {
    private final Map<String, Room> rooms;

    public static Map<String, Room> parseResponse(String responseMessage) {
        return new GsonBuilder().create().fromJson(responseMessage, GetTimeSlotByRoomResponse.class).rooms;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, GetTimeSlotByRoomResponse.class);
    }
}
