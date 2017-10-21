package GUI.panels.studentpanels;

import GUI.functions.HelperFunctions;
import GUI.functions.Message;
import GUI.panels.UserTerminalGUI;
import com.github.lgooddatepicker.components.DatePicker;
import domain.CampusName;
import domain.Room;
import domain.TimeSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Map;

class BookRoomPanel extends JPanel{
    private final StudentPanels parentPanel;
    private final UserTerminalGUI gui;
    private final JPanel selectionPanel;
    private final JTextField bookIdField;
    private final JComboBox<CampusBoxItem> campusBox;
    private final JComboBox<String> roomBox;
    private final JComboBox<TimeSlotItem> timeslotBox;
    private final DatePicker datePicker;
    private final JButton bookRoomButton;
    private final JButton backButton;

    private Map<String, Room> availableRooms;
    private CampusName campus;
    private Calendar calendar = null;
    private String room = null;
    private TimeSlot slot = null;

    BookRoomPanel(StudentPanels parentPanel, UserTerminalGUI gui, String cardName){
        this.parentPanel = parentPanel;
        this.gui = gui;
        this.parentPanel.add(this, cardName);
        this.setLayout(new BorderLayout());
        JLabel title = new JLabel("Book room");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        this.add(title, BorderLayout.NORTH);
        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());

        bookIdField = new JTextField();
        HelperFunctions.setDimension(bookIdField, parentPanel.getWidth(), 150);
        this.add(bookIdField, BorderLayout.CENTER);

        selectionPanel = new JPanel(new GridLayout(6, 1));
        HelperFunctions.setDimension(selectionPanel, parentPanel.getWidth(), 300);
        this.add(selectionPanel, BorderLayout.SOUTH);

        campusBox = new JComboBox<>();
        roomBox = new JComboBox<>();
        timeslotBox = new JComboBox<>();
        datePicker = new DatePicker();
        bookRoomButton = new JButton("Book the selected room");
        backButton = new JButton("Back");
        setSelectionPanel();
    }

    private void setSelectionPanel(){
        setCampusBox(0);
        setDatePickerPanel(1);
        setRoomPicker(2);
        setTimeslotPicker(3);
        setBookRoomButton(4);
        setBackButton(5);

    }

    private void setCampusBox(int index){
        campusBox.addItem(new CampusBoxItem(CampusName.DORVAL));
        campusBox.addItem(new CampusBoxItem(CampusName.WESTMOUNT));
        campusBox.addItem(new CampusBoxItem(CampusName.KIRKLAND));
        selectionPanel.add(campusBox, index);
    }

    private void setDatePickerPanel(int index){
        JPanel datePickerPanel = new JPanel(new BorderLayout());
        HelperFunctions.setDimension(datePickerPanel, parentPanel.getWidth(), 60);

        datePicker.setPreferredSize(new Dimension(500, 60));
        datePicker.setDateToToday();
        JButton getAvailableRoomsButton = new JButton("Refresh");
        HelperFunctions.setDimension(getAvailableRoomsButton, 80, 60);

        getAvailableRoomsButton.addActionListener(e->{
            //reset the room and time slot combo box first
            roomBox.removeAllItems();
            timeslotBox.removeAllItems();

            //get the selected date and parse into calendar object
            LocalDate date = datePicker.getDate();
            calendar = Calendar.getInstance();
            calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(),
                    0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            //get the selected campus name
            campus = ((CampusBoxItem) campusBox.getSelectedItem()).getCampusName();
            //set the campus of interested in gui
            gui.setCampusOfInterest(campus);

            //get all the available rooms using date and campus of interested
            availableRooms = gui.getClient().getAvailableTimeSlot(calendar, campus);

            if (availableRooms == null) {
                Message.optionPaneError("No room available on " + calendar.getTime() + " in " + campus.name, this);
                room = null;
                slot = null;
            }else{
                //Load the room combo box
                for(String key : availableRooms.keySet()) roomBox.addItem(key);

                //load the time slot combo box with the first room in the room box
                room = (String) roomBox.getSelectedItem();
            }
        });

        datePickerPanel.add(datePicker, BorderLayout.WEST);
        datePickerPanel.add(getAvailableRoomsButton, BorderLayout.EAST);

        selectionPanel.add(datePickerPanel, index);

    }

    private void setRoomPicker(int index){
        selectionPanel.add(roomBox, index);

        roomBox.addActionListener(e->{
            if(roomBox.getItemCount() != 0){
                //reset time slot combo box
                timeslotBox.removeAllItems();

                room = (String) roomBox.getSelectedItem();
                //load room
                for(TimeSlot slot : availableRooms.get(room).getTimeSlots()){
                    if(slot.getStudentID() == null){
                        timeslotBox.addItem(new TimeSlotItem(slot));
                    }
                }
                if (timeslotBox.getItemCount() != 0)
                    slot = ((TimeSlotItem) timeslotBox.getSelectedItem()).getTimeSlot();
                else slot = null;
            }
        });
    }

    private void setTimeslotPicker(int index){
        timeslotBox.addActionListener(e->{
            if(timeslotBox.getItemCount() != 0) slot = ((TimeSlotItem) timeslotBox.getSelectedItem()).getTimeSlot();
        });
        selectionPanel.add(timeslotBox, index);
    }

    private void setBookRoomButton(int index){
        selectionPanel.add(bookRoomButton, index);

        bookRoomButton.addActionListener(e->{
            if(calendar == null || room == null | slot == null)
                Message.optionPaneError("Please select data, room and time slot first", this);
            else{
                String result = gui.getClient().bookRoom(campus, room, calendar, slot, gui.getCampusOfTheID(), gui.getId());
                if(!result.startsWith("Error")) result = "Booking ID : " + result;

                bookIdField.setText(result);

                //reset all
                roomBox.removeAllItems();
                timeslotBox.removeAllItems();
                calendar = null;
                room = null;
                slot = null;
                availableRooms.clear();
                gui.setCampusOfInterest(null);
            }
        });
    }

    private void setBackButton(int index){
        selectionPanel.add(backButton, index);
        backButton.addActionListener(e->((CardLayout) parentPanel.getLayout()).show(parentPanel, "menu"));
    }

    @RequiredArgsConstructor
    @Getter
    private class CampusBoxItem{
        final CampusName campusName;

        @Override
        public String toString(){
            return campusName.name;
        }
    }

    @RequiredArgsConstructor
    @Getter
    private class TimeSlotItem{
        final TimeSlot timeSlot;

        @Override
        public String toString(){
            return timeSlot.getStartTime().getTime().toString() + " - " + timeSlot.getEndTime().getTime().toString();
        }

    }
}
