package domain;

import com.google.gson.GsonBuilder;
import com.sun.istack.internal.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;

@Getter
@RequiredArgsConstructor
public class BookingInfo {
    private final boolean toBook;
    private final String campusOfInterestAbrev;
    private final String studentCampusAbrev;
    private final int studentID;
    private final Calendar bookingDate;
    private final String roomName;
    private final Calendar bookingStartTime;
    private final Calendar bookingEndTime;

    @Nullable
    public static BookingInfo decode(String code) {
        char[] chars = code.toCharArray();
        for (int i = 0; i < chars.length; ++i) chars[i] -= i;
        String decode = new String(chars);
        if (!decode.contains("-")) {
            System.err.println("Invalid booking reference input");
            return null;
        }
        String[] delim = decode.split("-");
        if (delim.length != 9) {
            System.err.println("Invalid booking reference input");
            return null;
        }
        boolean toBook = Boolean.getBoolean(delim[0]);
        String campusAbrev = delim[1];
        String studentCampusAbrev = delim[2];

        int studentID = Integer.parseInt(delim[3]);

        Calendar bookingDate = Calendar.getInstance();
        bookingDate.setTimeInMillis(Long.parseLong(delim[4]));

        String roomName = delim[5];

        Calendar bookingStartTime = Calendar.getInstance();
        bookingStartTime.setTimeInMillis(Long.parseLong(delim[6]));

        Calendar bookingEndTime = Calendar.getInstance();
        bookingEndTime.setTimeInMillis(Long.parseLong(delim[7]));

        return new BookingInfo(
                toBook,
                campusAbrev,
                studentCampusAbrev,
                studentID,
                bookingDate,
                roomName,
                bookingStartTime,
                bookingEndTime
        );
    }

    public String toString() {
        return new GsonBuilder().create().toJson(this, BookingInfo.class);
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    public String encodeBookingID() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(toBook))
                .append("-")
                .append(campusOfInterestAbrev)
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
        for (int i = 0; i < chars.length; ++i) chars[i] += i;
        return new String(chars);
    }
}
