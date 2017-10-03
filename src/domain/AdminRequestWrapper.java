package domain;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;
import java.util.List;


@RequiredArgsConstructor
public class AdminRequestWrapper{
    private final String roomNumber;
    private final Calendar calendar;
    private final List<TimeSlot> slots;

    public String toString(){
        return new GsonBuilder().create().toJson(this, AdminRequestWrapper.class);
    }

    public byte[] toByteArray(){
        return toString().getBytes();
    }
}
