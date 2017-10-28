package service.server.admin;

import domain.Campus;
import domain.TimeSlot;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;
import java.util.List;

@RequiredArgsConstructor
public class CreateRoom{
    private final Campus adminCampus;
    private final Calendar date;
    private final List<TimeSlot> slotsList;


}
