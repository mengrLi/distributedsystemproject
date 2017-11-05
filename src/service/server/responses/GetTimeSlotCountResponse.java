package service.server.responses;

import com.google.gson.GsonBuilder;
import domain.Campus;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GetTimeSlotCountResponse {
    private final Map<Campus, Integer> roomCounts;

    public static Map<Campus, Integer> parseResponse(String responseMessage) {
        return new GsonBuilder().create().fromJson(responseMessage, GetTimeSlotCountResponse.class).roomCounts;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, GetTimeSlotCountResponse.class);
    }
}
