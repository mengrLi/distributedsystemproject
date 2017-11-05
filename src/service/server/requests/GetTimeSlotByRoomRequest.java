package service.server.requests;

import CampusServerCorba.CampusServerInterface;
import com.google.gson.GsonBuilder;
import domain.Campus;
import domain.Room;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import service.server.responses.GetTimeSlotByRoomResponse;

import java.util.Calendar;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class GetTimeSlotByRoomRequest {
    private final Calendar date;
    private final Campus campus;

    public Map<String, Room> sendRequest(CampusServerInterface campusInterface) {
        String responseMessage = campusInterface.getAvailableTimeSlotByRoom(toString());
        return GetTimeSlotByRoomResponse.parseResponse(responseMessage);
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, GetTimeSlotByRoomRequest.class);
    }

    public static GetTimeSlotByRoomRequest parseRequest(String responseMessage) {
        return new GsonBuilder().create().fromJson(responseMessage, GetTimeSlotByRoomRequest.class);
    }


}
