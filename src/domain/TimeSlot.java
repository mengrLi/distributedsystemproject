package domain;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.Serializable;
import java.util.Calendar;


public class TimeSlot implements Serializable, Comparable<TimeSlot>{
    @Getter
    private Calendar startTime;
    @Getter
    private Calendar endTime;
    @Getter
    private Integer studentID = null;
    @Getter
    private Campus studentCampus = null;
    @Getter
    private String bookingID = null;


    @Expose
    private String start;
    @Expose
    private String finish;


    public TimeSlot(Calendar startTime, Calendar endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        this.start = Format.formatTime(startTime);
        this.finish = Format.formatTime(endTime);
    }



    public void setEndTime(Calendar endTime){
        this.endTime = endTime;
        this.finish = Format.formatTime(endTime);
    }

    public long getStartMilli(){
        return startTime.getTimeInMillis();
    }


    public long getEndMilli(){
        return endTime.getTimeInMillis();
    }


    public void setStartTime(Calendar startTime){
        this.startTime = startTime;
        this.start = Format.formatTime(startTime);
    }

    public void setStudentID(Campus campusOfID, Integer studentID, String bookingID){
        this.studentID = studentID;
        this.studentCampus = campusOfID;
        this.bookingID = bookingID;
    }

    public void cancelBooking(){
        this.studentID = null;
        this.bookingID = null;
        this.studentCampus = null;
    }


    @Override
    public int compareTo(TimeSlot o){
        return (int) (this.startTime.getTimeInMillis() - o.startTime.getTimeInMillis());
    }

    @Override
    public boolean equals(Object obj){
        return this.startTime.equals(((TimeSlot) obj).startTime)
                && this.endTime.equals(((TimeSlot) obj).endTime);
    }

    public String toString(){
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
                .toJson(this, TimeSlot.class);
    }


}
