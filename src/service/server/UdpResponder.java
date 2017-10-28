package service.server;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.istack.internal.NotNull;
import domain.BookingInfo;
import domain.Room;
import lombok.RequiredArgsConstructor;
import service.server.student.BookRoom;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Map;

@RequiredArgsConstructor
public class UdpResponder implements Runnable {
    @NotNull
    private final DatagramSocket socket;
    @NotNull
    private final DatagramPacket request;
    @NotNull
    private final Server server;

    @Override
    public void run() {
        try {
            byte[] data = makeResponse();
            if (data == null) data = "Invalid".getBytes();
            DatagramPacket response = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
            socket.send(response);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private byte[] makeResponse() throws RemoteException {
        String json = new String(request.getData()).trim();
        String requestType = json.substring(2, 8);
        switch (requestType) {
            case "toBook":
                BookingInfo bookingInfo = new GsonBuilder().create().fromJson(json, BookingInfo.class);
                if (bookingInfo.isToBook()) {
//                        to book

                    BookRoom bookRoom = new BookRoom(bookingInfo, server);

                    return bookRoom.book().getBytes();
                } else {
                    //to cancel
//                        return String.valueOf(server.removeBookingRecord(bookingInfo)).getBytes();
                }
                return null; // temp
            case "getInt": {
                String[] delim = json.split("-");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTimeInMillis(Long.parseLong(delim[1]));
//                        int get = server.countFreeRooms(calendar);
                    int get = 0; // temp
                    return String.valueOf(get).getBytes();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            case "getMap": {
                String[] delim = json.split("-");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTimeInMillis(Long.parseLong(delim[1]));
                    Type type = new TypeToken<Map<String, Room>>() {
                    }.getType();
                    synchronized (server.getRoomLock()) {
                        Map<String, Room> ret = server.getRoomRecords().getRecordsOfDate(calendar);
                        return new GsonBuilder().create().toJson(ret, type).getBytes();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            case "remove": {
                String[] delim = json.split("-");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTimeInMillis(Long.parseLong(delim[1]));
                    int id = Integer.parseInt(delim[2]);
                    synchronized (server.getRoomLock()) {
//                            int count = server.getStudentBookingRecords().get(calendar.getTimeInMillis()).get(id);
//                            server.getStudentBookingRecords().get(calendar.getTimeInMillis()).put(id, count - 1);
                        return "true".getBytes();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return null;
            }
            case "switch": {
                System.out.println("SWITCH NOT DONE YET");
                return null;
            }
            default:
                System.err.println("Invalid udp request message");
                return null;
        }
    }
}
