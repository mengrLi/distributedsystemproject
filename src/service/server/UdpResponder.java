package service.server;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.istack.internal.NotNull;
import domain.BookingInfo;
import domain.Room;
import lombok.RequiredArgsConstructor;

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
        System.out.println(server.getCampus().name + " received request from " + request.getSocketAddress());
        String json = new String(request.getData()).trim();
        System.out.println(server.getCampus().name + "'s Responder received json message : " + json);
        String requestType = json.substring(2, 8);
        String response;
        switch (requestType) {
            case "toBook": {
                BookingInfo bookingInfo = new GsonBuilder().create().fromJson(json, BookingInfo.class);
                if (bookingInfo.isToBook()) {
                    //to book
                    synchronized (server.getRoomLock()) {
                        System.out.println(server.getCampus().name + ": booking request processing");
                        response = server.getRoomRecords().bookRoom(bookingInfo);
                        synchronized (server.getLogLock()) {
                            server.getLogFile().info(
                                    "\n" + server.getCampus().name + " received booking request from "
                                            + request.getSocketAddress()
                                            + "\n" + response
                            );
                        }
                        return response.getBytes();
                    }
                } else {
                    //to cancel
                    synchronized (server.getRoomLock()) {
                        System.out.println(server.getCampus().name + ": cancel booking request processing");
                        response = server.getRoomRecords().cancelBooking(bookingInfo);
                        synchronized (server.getLogLock()) {
                            server.getLogFile().info(
                                    "\n" + server.getCampus().name + " received cancel booking request from "
                                            + request.getSocketAddress()
                                            + "\n" + response
                            );
                        }
                        return response.getBytes();
                    }
                }
            }
            case "getInt": {
                String[] delim = json.split("-");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTimeInMillis(Long.parseLong(delim[1]));
                    int get;
                    synchronized (server.getRoomLock()) {
                        get = server.getRoomRecords().getAvailableTimeSlotsCountOfDate(calendar);
                    }
                    synchronized (server.getLogLock()) {
                        server.getLogFile().info(
                                "\n" + server.getCampus().name
                                        + " received available room count request from "
                                        + request.getSocketAddress()
                                        + "\n" + get + " rooms found"
                        );
                    }
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
                    Map<String, Room> ret;
                    synchronized (server.getRoomLock()) {
                        ret = server.getRoomRecords().getRecordsOfDate(calendar);
                    }
                    synchronized (server.getLogLock()) {
                        server.getLogFile().info(
                                "\n" + server.getCampus().name + " received available room details request from "
                                        + request.getSocketAddress()
                                        + "\n" + ret.size() + " rooms have been found"
                        );
                    }
                    return new GsonBuilder().create().toJson(ret, type).getBytes();
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
                    int modifyResult;
                    synchronized (server.getRoomLock()) {
                        modifyResult = server.getStudentBookingRecords()
                                .modifyBookingRecords(calendar, id, delim[3], false);
                    }
                    synchronized (server.getLogLock()) {
                        server.getLogFile().info(
                                "\n" + server.getCampus().name + " received remove student booking record from "
                                        + request.getSocketAddress()
                        );
                    }
                    if (modifyResult == -1 || modifyResult == 4) {
                        return "false".getBytes();
                    } else return "true".getBytes();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return null;
            }
            case "chkCnl": {
                String bookingID = json.substring(8);
                BookingInfo info = BookingInfo.decode(bookingID);

                boolean b;
                synchronized (server.getRoomLock()) {
                    b = server.getRoomRecords().validateBooking(info, bookingID);
                }
                synchronized (server.getLogLock()) {
                    server.getLogFile().info(
                            "\n" + server.getCampus().name + " received room cancellation check request from "
                                    + request.getSocketAddress()
                                    + "\nBooking" + (b ? " " : " not ") + "found"
                    );
                }
                return String.valueOf(b).getBytes();
            }
            default: {
                System.err.println("Invalid udp request message");
                return null;
            }
        }
    }
}
