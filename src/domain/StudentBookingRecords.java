package domain;

import lombok.Getter;
import service.server.Server;

import java.util.*;

public class StudentBookingRecords{
    @Getter private final Map<Calendar, Map<Integer, List<String>>> records;
    private final Campus campus;

    public StudentBookingRecords(Server server, Campus campus){
        this.campus = campus;
        this.records = new HashMap<>();
    }

    /**
     * get or default to 0
     *
     * @param date
     * @param studentID
     * @return
     */
    public int getWeeklyBookingRecords(Calendar date, int studentID) {
        Calendar startOfWeek = CalendarHelpers.getStartOfWeek(date);

        Map<Integer, List<String>> allWeeklyRecords = records.get(startOfWeek);
        if (allWeeklyRecords == null) {
            System.out.println("no weekly record for " + campus.abrev + "s" + studentID);
            return 0;
        }
        else {
            List<String> studentWeeklyRecord = allWeeklyRecords.get(studentID);
            if (studentWeeklyRecord == null) {
                System.out.println("no record for " + campus.abrev + "s" + studentID + " in the week of " + startOfWeek.getTime());
                return 0;
            }
            else return studentWeeklyRecord.size();
        }
    }

    /**
     *
     * @param date
     * @param studentID
     * @param bookingID
     * @return number of bookings, if -1, means not added, over limit
     */
    public int modifyBookingRecords(Calendar date, int studentID, String bookingID, boolean add) {
        Calendar startOfWeek = CalendarHelpers.getStartOfWeek(date);
        Map<Integer, List<String>> week = records.getOrDefault(startOfWeek, new HashMap<>());
        List<String> bookingIdList = week.getOrDefault(studentID, new LinkedList<>());
        if (add) {
            bookingIdList.add(bookingID);
            week.put(studentID, bookingIdList);
            records.put(startOfWeek, week);
        } else {
            if (bookingIdList.size() == 0) return 4;//should not be reached
            else {
                int index = -1;
                for (int i = 0, size = bookingIdList.size(); i < size; ++i) {
                    if (bookingIdList.get(i).equals(bookingID)) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    bookingIdList.remove(index);
                    return -1;
                }
            }
        }
        return 3 - bookingIdList.size();
    }
}
