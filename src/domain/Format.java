package domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Format{

    public static String formatTime(Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(calendar.getTime());
    }

    public static String formatDate(Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(calendar.getTime());
    }
}
