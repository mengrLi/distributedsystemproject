package service.server.responses;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.Campus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class GetTimeSlotCountResponse {
    private final Map<Campus, Integer> resposne;

    public GetTimeSlotCountResponse(String jsonMessage) {
        resposne = new GsonBuilder().create().fromJson(jsonMessage, new TypeToken<Map<Campus, Integer>>() {
        }.getType());
    }
}
