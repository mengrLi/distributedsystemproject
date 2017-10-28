package service.server.student;

import domain.Campus;
import domain.TimeSlot;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;

@RequiredArgsConstructor
public class SwitchRoom{
    private final String studentID;
    private final String bookingID;
    private final Campus newCampus;
    private final Calendar newDate;
    private final TimeSlot newSlot;

}
