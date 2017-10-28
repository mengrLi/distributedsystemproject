package service.server.student;

import domain.Campus;
import lombok.RequiredArgsConstructor;
import service.server.UdpRequest;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CheckRoom{
    private final Campus campus;
    private final Calendar date;

    public CheckRoom(Calendar date){
        this(null, date);
    }


    public Map<String, Integer> getAllCampusRoomAvaiabilityCounts(){
        Map<String, Integer> ret = new HashMap<>();
        //TODO get all the campus count
        String requestString = "";

        //UDP connections
        UdpRequest udpRequest;
        for(Campus campus : Campus.values()){
//            udpRequest = new UdpRequest(requestString, campus);
//            String response = udpRequest.getResponse();
            //probll need to parse?
//            ret.putAll();
        }
        return ret;
    }









//    private int checkFreeRooms(Calendar date, Campus name){
//        if(name.name.equals(this.campus.name)){
//            return countFreeRooms(date);
//        }else{
//            int port = determinePort(name.abrev);
//            String req = "**countA-" + date.getTimeInMillis();
//            String response = udpRequest(req.getBytes(), req.length(), port).trim();
//            try{
//                return Integer.parseInt(response);
//            }catch(NumberFormatException e){
//                System.out.println(e.getMessage());
//                e.printStackTrace();
//                return -1;
//            }
//        }
//    }
//
//    private int countFreeRooms(Calendar calendar){
//        synchronized(roomLock){
//            Map<String, Room> getDate = roomRecord.get(calendar);
//            int counter = 0;
//            if (getDate == null) return 0;
//            for(Map.Entry<String, Room> entry : getDate.entrySet())
//                for(TimeSlot slot : entry.getValue().getTimeSlots())
//                    if(slot.getStudentID() == null) ++counter;
//            return counter;
//        }
//    }
//
//    @Override
//    public Map<String, Room> getAvailableTimeSlot(Calendar date, Campus campus) throws RemoteException{
//        if(campus.equals(this.campus)){
//            synchronized(roomLock){
//                return roomRecord.get(date);
//            }
//        }else{
//            String req = "**countB-" + date.getTimeInMillis();
//            return
//                    new GsonBuilder()
//                            .create()
//                            .fromJson(
//                                    udpRequest(
//                                            req.getBytes(),
//                                            req.length(),
//                                            campus.udpPort).trim(),
//                                    new TypeToken<Map<String, Room>>(){
//                                    }.getType()
//                            );
//        }
//    }




}
