package service.server.requests;

import CampusServerCorba.CampusServerInterface;
import com.google.gson.GsonBuilder;
import domain.Campus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import service.server.responses.GetTimeSlotCountResponse;

import java.util.Calendar;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class GetTimeSlotCountRequest {
    private final Calendar date;

    public Map<Campus, Integer> sendRequest(CampusServerInterface campusInterface) {
        String responseMessage = campusInterface.getAvailableTimeSlotCount(toString());
        return GetTimeSlotCountResponse.parseResponse(responseMessage);
    }
    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, GetTimeSlotCountRequest.class);
    }

    public static GetTimeSlotCountRequest parseRequest(String requestMessage) {
        return new GsonBuilder().create().fromJson(requestMessage, GetTimeSlotCountRequest.class);
    }

}
