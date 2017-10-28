package service.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpResponder implements Runnable {
    private DatagramSocket socket = null;
    private DatagramPacket request = null;

    UdpResponder(DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.request = packet;
    }

    @Override
    public void run() {
//            try {
//                byte[] data = makeResponse();
//                if(data != null){
//                    DatagramPacket response = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
//                    socket.send(response);
//                }
//            } catch (IOException e) {
//                System.err.println(e.getMessage());
//                e.printStackTrace();
//            }
    }

//        private byte[] makeResponse() {
//            String json = new String(request.getData()).trim();
//            String requestType = json.substring(2, 8);
//            switch (requestType) {
//                case "toBook":
//                    BookingInfo bookingInfo = new GsonBuilder().create().fromJson(json, BookingInfo.class);
//                    if (bookingInfo.isToBook()) {
//                        //to book
//                        return server.bookRoomHelperPrivate(bookingInfo).getBytes();
//                    } else {
//                        //to cancel
//                        return String.valueOf(server.removeBookingRecord(bookingInfo)).getBytes();
//                    }
//                case "countA": {
//                    String[] delim = json.split("-");
//                    Calendar calendar = Calendar.getInstance();
//                    try {
//                        calendar.setTimeInMillis(Long.parseLong(delim[1]));
//                        int get = server.countFreeRooms(calendar);
//                        return String.valueOf(get).getBytes();
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                }
//                case "countB": {
//                    String[] delim = json.split("-");
//                    Calendar calendar = Calendar.getInstance();
//                    try {
//                        calendar.setTimeInMillis(Long.parseLong(delim[1]));
//                        Type type = new TypeToken<Map<String, Room>>() {
//                        }.getType();
//                        synchronized (server.roomLock) {
//                            Map<String, Room> ret = server.roomRecord.get(calendar);
//                            return new GsonBuilder().create().toJson(ret, type).getBytes();
//                        }
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                }
//                case "remove": {
//                    String[] delim = json.split("-");
//                    Calendar calendar = Calendar.getInstance();
//                    try {
//                        calendar.setTimeInMillis(Long.parseLong(delim[1]));
//                        int id = Integer.parseInt(delim[2]);
//                        synchronized (server.) {
//                            int count = studentBookingRecord.get(calendar.getTimeInMillis()).get(id);
//                            studentBookingRecord.get(calendar.getTimeInMillis()).put(id, count - 1);
//                        }
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                }
//                case "switch":{
//                    System.out.println("SWITCH NOT DONE YET");
//                    return null;
//                }
//                default:
//                    System.err.println("Invalid udp request message");
//                    return null;
//            }
//        }
}
