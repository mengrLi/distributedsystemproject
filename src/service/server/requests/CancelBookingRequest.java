package service.server.requests;

import CampusServerCorba.CampusServerInterface;
import com.google.gson.GsonBuilder;
import domain.Campus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CancelBookingRequest {
    private final String bookingId;
    private final Campus campus;
    private final int id;

    public String sendResquest(CampusServerInterface campusInterface) {
        return campusInterface.cancelBooking(toString());
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, CancelBookingRequest.class);
    }

    public static CancelBookingRequest parseRequest(String requestMessage) {
        return new GsonBuilder().create().fromJson(requestMessage, CancelBookingRequest.class);
    }
}
