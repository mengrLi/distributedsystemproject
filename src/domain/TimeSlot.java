package domain;

public class TimeSlot{
    private int startTime;
    private int end;
    private Integer studentID;

    public TimeSlot(int start, int end, Integer id){
        this.startTime = start;
        this.end = end;
        this.studentID = id;
    }

    public int getStartTime(){
        return startTime;
    }

    public int getEnd(){
        return end;
    }

    public Integer getStudentID(){
        return studentID;
    }
}
