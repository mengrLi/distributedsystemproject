package service.server.responses;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class SwitchRoomResponse {
    private final Map<String, String> switchResult;

    public static Map<String, String> parseResponse(String responseMessage) {
        return new GsonBuilder().create().fromJson(responseMessage, SwitchRoomResponse.class).switchResult;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, SwitchRoomResponse.class);
    }
}
