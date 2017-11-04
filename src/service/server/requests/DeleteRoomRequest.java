package service.server.requests;

import CampusServerCorba.CampusServerInterface;
import com.google.gson.GsonBuilder;
import domain.TimeSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import service.server.responses.DeleteRoomResponse;

import java.util.Calendar;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class DeleteRoomRequest {
    private final String roomNumber;
    private final Calendar date;
    private final List<TimeSlot> list;
    private final String fullID;

    public List<List<TimeSlot>> sendRequest(CampusServerInterface campusInterface) {
        String responseMessage = campusInterface.deleteRoom(toString());
        return DeleteRoomResponse.parseResponse(responseMessage);
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, DeleteRoomRequest.class);
    }

    public static DeleteRoomRequest parseRequest(String requestMessage) {
        return new GsonBuilder().create().fromJson(requestMessage, DeleteRoomRequest.class);
    }
}
