package domain;

import java.io.Serializable;
import java.util.Calendar;


public class TimeSlot implements Serializable{
    private Calendar startTime;
    private Calendar end;
    private Integer studentID;

    public TimeSlot(Calendar start, Calendar end, Integer id){
        this.startTime = start;
        this.end = end;
        this.studentID = id;
    }

    public Calendar getStartTime(){
        return startTime;
    }

    public Calendar getEnd(){
        return end;
    }

    public Integer getStudentID(){
        return studentID;
    }
}
