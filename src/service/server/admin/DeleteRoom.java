package service.server.admin;

import domain.Campus;
import domain.TimeSlot;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DeleteRoom{
    private final Campus adminCampus;
    private final List<TimeSlot> slotsList;
}

