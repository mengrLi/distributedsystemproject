package domain;

import java.util.Calendar;

public class CalendarHelpers{
    public static Calendar getStartOfWeek(Calendar calendar){
        Calendar startOfWeek = (Calendar) calendar.clone();
        startOfWeek.set(Calendar.HOUR, 0);
        startOfWeek.set(Calendar.MINUTE,0);
        startOfWeek.set(Calendar.SECOND,0);
        startOfWeek.set(Calendar.MILLISECOND,0);
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.getFirstDayOfWeek());

        return startOfWeek;
    }
}
