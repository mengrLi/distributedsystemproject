package domain;

import java.util.Calendar;

/**
 * super sample string encoder.. and decoder
 */
public class RandomString{

    public static String encode(String campusAbrev, String studentCampusAbrev, int studentID,
                                Calendar bookingDate, String roomName,
                                Calendar bookingStartTime, Calendar bookingEndTime){
        StringBuilder builder = new StringBuilder();
        builder.append(campusAbrev)
                .append("-")
                .append(studentCampusAbrev)
                .append("-")
                .append(studentID)
                .append('-')
                .append(bookingDate.getTimeInMillis())
                .append("-")
                .append(roomName)
                .append("-")
                .append(bookingStartTime.getTimeInMillis())
                .append("-")
                .append(bookingEndTime.getTimeInMillis())
                .append("-")
                .append(System.currentTimeMillis());
        char[] chars = builder.toString().toCharArray();
        for(int i = 0; i < chars.length; ++i) chars[i] += i;
        return new String(chars);
    }

    public static String decode(String code){
        char[] chars = code.toCharArray();
        for(int i = 0; i < chars.length; ++i) chars[i] -= i;
        return new String(chars);
    }

    public static void main(String[] args){
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2000, 0, 1, 0, 0, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2000, 0, 1, 1, 0, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(2000, 0, 1, 2, 0, 0);
        calendar3.set(Calendar.MILLISECOND, 0);
        String code = encode("abc", "def", 1111, calendar1, "abc", calendar2, calendar3);
        System.out.println(code);
        String decode = decode(code);
        System.out.println(decode);
    }
}