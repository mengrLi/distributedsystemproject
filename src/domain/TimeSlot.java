package domain;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Calendar;


public class TimeSlot implements Serializable, Comparable<TimeSlot>{
    private Calendar startTime;
    private Calendar endTime;
    private Integer studentID = null;
    private CampusName studentCampus = null;

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

    public Calendar getStartTime(){
        return startTime;
    }

    public Calendar getEndTime(){
        return endTime;
    }

    public void setEndTime(Calendar endTime){
        this.endTime = endTime;
        this.finish = Format.formatTime(endTime);
    }

    public long getStartMilli(){
        return startTime.getTimeInMillis();
    }

    public Integer getStudentID(){
        return studentID;
    }

    public long getEndMilli(){
        return endTime.getTimeInMillis();
    }

    public void setStartTime(Calendar startTime){
        this.startTime = startTime;
        this.start = Format.formatTime(startTime);
    }

    public void setStudentID(Integer studentID){
        this.studentID = studentID;
    }

    public void cancelBooking(){
        //TODO reduce student booking count
        this.studentID = null;
    }


    @Override
    public int compareTo(TimeSlot o){
        return (int) (this.startTime.getTimeInMillis() - o.startTime.getTimeInMillis());
    }

    public String toString(){
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
                .toJson(this, TimeSlot.class);
    }


}
