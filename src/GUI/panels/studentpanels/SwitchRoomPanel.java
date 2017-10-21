package GUI.panels.studentpanels;

import GUI.functions.HelperFunctions;
import GUI.functions.Message;
import GUI.panels.UserTerminalGUI;
import com.github.lgooddatepicker.components.DatePicker;
import domain.BookingInfo;
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

class SwitchRoomPanel extends JPanel {
    private final StudentPanels parentPanel;
    private final UserTerminalGUI gui;

    private final JTextField bookingIDField;
    private final JButton enterBookingIdButton;
    private final JPanel leftPanel, rightPanel;
    private final JLabel studentIDLabel, bookedCampusLabel, dateLabel, startTimeLabel, endTimeLabel;

    private final JComboBox<CampusNameBox> campusComboBox;
    private final DatePicker datePicker;
    private final JButton refreshDateButton;
    private final JComboBox<RoomNameBox> roomComboBox;
    private final JComboBox<TimeSlotBox> timeslotComboBox;
    private final JButton submitButton;
    private final JButton backButton;


    private Calendar calendar;
    private Map<String, Room> roomMap;

    SwitchRoomPanel(StudentPanels parentPanel, UserTerminalGUI gui, String cardName) {
        this.parentPanel = parentPanel;
        this.gui = gui;
        this.parentPanel.add(this, cardName);

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel bookingIDLabel = new JLabel("Enter Booking ID:");
        bookingIDLabel.setFont(new Font("Serif", Font.BOLD, 20));
        topPanel.add(bookingIDLabel, BorderLayout.WEST);

        bookingIDField = new JTextField();
        HelperFunctions.setDimension(bookingIDField, 450, 50);
        topPanel.add(bookingIDField, BorderLayout.CENTER);

        enterBookingIdButton = new JButton("Enter");
        enterBookingIdButton.addActionListener(e -> enterBookingIdButtonListener());
        topPanel.add(enterBookingIdButton, BorderLayout.EAST);

        this.add(topPanel, BorderLayout.NORTH);

        JPanel botPanel = new JPanel(new BorderLayout());
        this.add(botPanel, BorderLayout.SOUTH);

        leftPanel = new JPanel(new GridLayout(5, 1));
        studentIDLabel = new Labels("ID: n/a");
        bookedCampusLabel = new Labels("Campus: n/a");
        dateLabel = new Labels("Date: n/a");
        startTimeLabel = new Labels("Start: n/a");
        endTimeLabel = new Labels("End: n/a");

        leftPanel.add(studentIDLabel, 0);
        leftPanel.add(bookedCampusLabel, 1);
        leftPanel.add(dateLabel, 2);
        leftPanel.add(startTimeLabel, 3);
        leftPanel.add(endTimeLabel, 4);

        botPanel.add(leftPanel, BorderLayout.WEST);

        JPanel midPanel = new JPanel(new BorderLayout());
        HelperFunctions.setDimension(midPanel, 200, 450);
        botPanel.add(midPanel, BorderLayout.CENTER);

        //Bot right panel
        rightPanel = new JPanel(new GridLayout(7, 1));
        campusComboBox = new JComboBox<>();
        setCampusComboBox();

        datePicker = new DatePicker();
        datePicker.setDateToToday();

        refreshDateButton = new JButton("Load rooms");
        refreshDateButton.addActionListener(e -> refreshDateButtonListener());

        roomComboBox = new JComboBox<>();
        roomComboBox.addActionListener(e -> roomComboBoxListener());

        timeslotComboBox = new JComboBox<>();

        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitButtonListener());

        backButton = new JButton("Back");
        backButton.addActionListener(e -> ((CardLayout) parentPanel.getLayout()).show(parentPanel, "menu"));
        rightPanel.add(campusComboBox, 0);
        rightPanel.add(datePicker, 1);
        rightPanel.add(refreshDateButton, 2);
        rightPanel.add(roomComboBox, 3);
        rightPanel.add(timeslotComboBox, 4);
        rightPanel.add(submitButton, 5);
        rightPanel.add(backButton, 6);

        botPanel.add(rightPanel, BorderLayout.EAST);
    }

    private void setCampusComboBox() {
        campusComboBox.addItem(new CampusNameBox(CampusName.DORVAL));
        campusComboBox.addItem(new CampusNameBox(CampusName.WESTMOUNT));
        campusComboBox.addItem(new CampusNameBox(CampusName.KIRKLAND));
    }

    private void refreshDateButtonListener() {
        calendar = Calendar.getInstance();
        LocalDate date = datePicker.getDate();
        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        roomMap = gui.getClient().getAvailableTimeSlot(calendar, ((CampusNameBox) campusComboBox.getSelectedItem()).getCampus());
        roomComboBox.removeAllItems();
        for (Room room : roomMap.values()) roomComboBox.addItem(new RoomNameBox(room));
    }

    private void roomComboBoxListener() {

        if (roomComboBox.getItemCount() != 0) {
            timeslotComboBox.removeAllItems();
            Room room = ((RoomNameBox) roomComboBox.getSelectedItem()).getRoom();
            for (TimeSlot slot : room.getTimeSlots()) {
                if (slot.getStudentID() == null) timeslotComboBox.addItem(new TimeSlotBox(slot));
            }
        }
    }

    //    private void timeslotComboBoxListener() {
//        if(timeslotComboBox.getItemCount()!=0){
//
//        }
//    }
    private void submitButtonListener() {

    }


    private void enterBookingIdButtonListener() {
        if (bookingIDField.getText().equals(""))
            Message.optionPaneError("Please enter your booking ID first", this);
        else {
            BookingInfo bookingInfo = BookingInfo.decode(bookingIDField.getText());
            if (bookingInfo == null)
                Message.optionPaneError("Booking ID contains errors", this);
            else {
                studentIDLabel.setText(gui.getFullID());
                bookedCampusLabel.setText("Campus: " + CampusName.getCampusName(bookingInfo.getCampusOfInterestAbrev()));
                dateLabel.setText("Date: " + bookingInfo.getBookingDate().getTime());
                startTimeLabel.setText("Start: " +
                        bookingInfo.getBookingStartTime().get(Calendar.HOUR_OF_DAY) +
                        " : " +
                        bookingInfo.getBookingStartTime().get(Calendar.MINUTE)
                );
                endTimeLabel.setText("End: " +
                        bookingInfo.getBookingEndTime().get(Calendar.HOUR_OF_DAY) +
                        " : " +
                        bookingInfo.getBookingEndTime().get(Calendar.MINUTE)
                );
            }
        }
    }

    class Labels extends JLabel {
        Labels(String text) {
            super(text);
            setFont(new Font("Serif", Font.PLAIN, 18));
        }
    }

    @RequiredArgsConstructor
    @Getter
    private class CampusNameBox {
        private final CampusName campus;

        @Override
        public String toString() {
            return campus.name;
        }
    }

    @RequiredArgsConstructor
    @Getter
    private class RoomNameBox {
        private final Room room;

        @Override
        public String toString() {
            return room.getRoomNumber();
        }
    }

    @RequiredArgsConstructor
    @Getter
    private class TimeSlotBox {
        private final TimeSlot timeSlot;

        @Override
        public String toString() {
            return timeSlot.getStartTime().get(Calendar.HOUR_OF_DAY) +
                    ":" +
                    timeSlot.getStartTime().get(Calendar.MINUTE) +
                    " to " +
                    timeSlot.getEndTime().get(Calendar.HOUR_OF_DAY) +
                    ":" +
                    timeSlot.getEndTime().get(Calendar.MINUTE);
        }
    }
}
