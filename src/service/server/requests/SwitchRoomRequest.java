package service.server.requests;

import CampusServerCorba.CampusServerInterface;
import com.google.gson.GsonBuilder;
import domain.Campus;
import domain.TimeSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import service.server.responses.SwitchRoomResponse;

import java.util.Calendar;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class SwitchRoomRequest {
    private final String bookingID;
    private final int studentID;
    private final Campus campus;
    private final Calendar date;
    private final TimeSlot slot;
    private final String roomIdentifier;

    public Map<String, String> sendRequest(CampusServerInterface campusInterface) {
        String responseMessage = campusInterface.switchRoom(toString());
        return SwitchRoomResponse.parseResponse(responseMessage);
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, SwitchRoomRequest.class);
    }

    public static SwitchRoomRequest parseRequest(String requestMessage) {
        return new GsonBuilder().create().fromJson(requestMessage, SwitchRoomRequest.class);
    }
}
