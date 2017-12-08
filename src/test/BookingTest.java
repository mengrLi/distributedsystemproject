package test;

import domain.Campus;
import domain.TimeSlot;
import lombok.RequiredArgsConstructor;
import user_v2.ClientV2;
import user_v2.StudentClientV2;

import java.util.Calendar;

/**
 * Synchronization test for booking same room by 30 students from different campuses
 */
public class BookingTest {

    public static void main(String[] args) {
        for(int i = 1000 ; i < 9999 ; i+=1000){
            for(int j = 0 ; j  <3 ; ++j){
                new Thread(new bookThread(j,i,4)).start();
            }
        }
    }
}
@RequiredArgsConstructor
class bookThread implements Runnable{
    private final int campusIndex;
    private final int id;
    private static Campus[] campuses = Campus.values();
    private final int date;
    @Override
    public void run() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 11, date, 0,0,0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar time1 = (Calendar) calendar.clone();
        time1.set(2017, 11, date, 8 , 0 ,0);
        Calendar time2 = (Calendar) calendar.clone();
        time2.set(2017, 11, date, 9 ,59,0);


        ClientV2 clientV2 = new StudentClientV2(campuses[campusIndex], id);
        String resposne = clientV2.bookRoom(campuses[0], "1", calendar, new TimeSlot(time1, time2),campuses[campusIndex],  id);
        System.out.println(campuses[campusIndex].abrev+"s"+id);
        System.out.println(resposne + "\n");
    }
}

