package service.server.requests;

import CampusServerCorba.CampusServerInterface;
import com.google.gson.GsonBuilder;
import domain.TimeSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import service.server.responses.CreateRoomResponse;

import java.util.Calendar;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CreateRoomRequest {
    private final String roomNumber;
    private final Calendar date;
    private final List<TimeSlot> list;
    private final String fullID;

    public static CreateRoomRequest parseRequest(String json) {
        return new GsonBuilder().create().fromJson(json, CreateRoomRequest.class);
    }

    public List<List<TimeSlot>> sendRequest(CampusServerInterface campusServerInterface) {
        String responseMessage = campusServerInterface.createRoom(toString());
        return CreateRoomResponse.parseResponse(responseMessage);
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, CreateRoomRequest.class);
    }
}
