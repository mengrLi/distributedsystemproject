import lombok.RequiredArgsConstructor;
import org.omg.CORBA.ORB;
import service.server.CampusServerCorba.CampusServerInterfacePOA;

@RequiredArgsConstructor
public class CampusServerObj extends CampusServerInterfacePOA {
    final private ORB orb;

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public String createRoom(String json) {
        return "create";
    }

    @Override
    public String deleteRoom(String json) {
        return "delete";
    }

    @Override
    public String bookRoom(String json) {
        return "book";
    }

    @Override
    public String switchRoom(String json) {
        return "switch";
    }

    @Override
    public String getAvailableTimeSlotCount(String json) {
        return "getCount";
    }

    @Override
    public String getAvailableTimeSlotByRoom(String json) {
        return "getRoom";
    }

    @Override
    public String cancelBooking(String json) {
        return "cancel";
    }

    @Override
    public String checkAdminId(String json) {
        return "check";
    }
}
