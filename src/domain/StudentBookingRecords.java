package domain;

import lombok.Getter;
import service.server.Server;

import java.util.*;

public class StudentBookingRecords{
    @Getter private final Map<Calendar, Map<Integer, List<String>>> records;
    private final Campus campus;
    private final Server server;

    public StudentBookingRecords(Server server, Campus campus){
        this.campus = campus;
        this.server = server;
        this.records = new HashMap<>();
    }

    /**
     * get or default to 0
     *
     * @param date
     * @param studentID
     * @return
     */
    public int getStudentWeeklyBookingRecords(Calendar date, int studentID){
        Calendar startOfWeek = CalendarHelpers.getStartOfWeek(date);
        synchronized(server.getLogLock()){
            server.getLogFile().info("Student " + campus.abrev+"s"+studentID + " checked his booking limit");
        }
        Map<Integer, List<String>> allWeeklyRecords = records.get(startOfWeek);
        if (allWeeklyRecords == null) return 0;
        else {
            List<String> studentWeeklyRecord = allWeeklyRecords.get(studentID);
            if (studentWeeklyRecord == null) return 0;
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
    public int modifyStudentBookingRecords(Calendar date, int studentID, String bookingID, boolean add){
        Calendar startOfWeek = CalendarHelpers.getStartOfWeek(date);
        Map<Integer, List<String>> week = records.getOrDefault(startOfWeek, new HashMap<>());
        List<String> bookingIdList = week.getOrDefault(studentID, new LinkedList<>());
        if (add) {
            if (bookingIdList.size() == 3) return -1;
            else {
                bookingIdList.add(bookingID);
                week.put(studentID, bookingIdList);
                records.put(date, week);
            }
        } else {
            if (bookingIdList.size() == 0) return 4;
            else {
                int index;
                for (int i = 0, size = bookingIdList.size(); i < size; ++i) {
                    if (bookingIdList.get(i).equals(bookingID)) {
                        index = i;
                        bookingIdList.remove(index);
                        break;
                    }
                }
            }
        }
        return 3 - bookingIdList.size();
    }
}
