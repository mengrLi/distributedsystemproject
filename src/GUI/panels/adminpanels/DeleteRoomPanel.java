package GUI.panels.adminpanels;

import GUI.UserTerminalGUI;
import GUI.functions.Message;
import com.github.lgooddatepicker.components.DatePicker;
import domain.Room;
import domain.TimeSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

public class DeleteRoomPanel extends JPanel {
    private final AdminPanels parentPanel;
    private final UserTerminalGUI gui;
    private final DatePicker datePicker;
    private final JComboBox<RoomComboItem> roomComboBox;
    private final JButton loadTimeSlotsButton;

    private final JTable currentTable, deleteTable;
    private final DefaultTableModel currentTableModel, deleteTableModel;

    private final JButton toRightButton, toLeftButton;
    private final JButton deleteSelectedButton, backButton;

    private Map<String, Room> roomMap;
    private Calendar calendar;

    public DeleteRoomPanel(AdminPanels adminPanels, UserTerminalGUI gui, String cardName) {
        this.parentPanel = adminPanels;
        this.gui = gui;
        this.setLayout(new BorderLayout());
        adminPanels.add(this, cardName);


        //Mid panel
        String[] header = new String[3];
        header[0] = "Start";
        header[1] = "End";
        header[2] = "Booked";

        currentTable = new JTable();
        currentTableModel = new DefaultTableModel(header, 0);
        currentTable.setModel(currentTableModel);
        this.add(new JScrollPane(currentTable), BorderLayout.WEST);

        deleteTable = new JTable();
        deleteTableModel = new DefaultTableModel(header, 0);
        deleteTable.setModel(deleteTableModel);
        this.add(new JScrollPane(deleteTable), BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout());

        //top panel
        JPanel midTopPanel = new JPanel(new GridLayout(4, 1));

        JLabel title = new JLabel("Delete Rooms");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        midTopPanel.add(title, 0);

        datePicker = new DatePicker();
        datePicker.setDateToToday();
        midTopPanel.add(datePicker, 1);

        loadTimeSlotsButton = new JButton("Load time slots");
        loadTimeSlotsButton.addActionListener(e -> loadTimeSlotsButtonListener());
        midTopPanel.add(loadTimeSlotsButton, 2);

        roomComboBox = new JComboBox<>();
        roomComboBox.addActionListener(e -> roomComboBoxListener());
        midTopPanel.add(roomComboBox, 3);

        centerPanel.add(midTopPanel, BorderLayout.NORTH);

        toLeftButton = new JButton("Remove");
        toLeftButton.addActionListener(e -> toLeftButtonListener());
        toRightButton = new JButton("Add");
        toRightButton.addActionListener(e -> toRightButtonListener());
        backButton = new JButton("Back");
        backButton.addActionListener(e -> ((CardLayout) parentPanel.getLayout()).show(parentPanel, "menu"));
        deleteSelectedButton = new JButton("Submit");
        deleteSelectedButton.addActionListener(e -> deleteSelectedButtonListener());

        JPanel midBotPanel = new JPanel(new GridLayout(4, 1));
        midBotPanel.add(toRightButton, 0);
        midBotPanel.add(toLeftButton, 1);
        midBotPanel.add(deleteSelectedButton, 2);
        midBotPanel.add(backButton, 3);

        centerPanel.add(midBotPanel, BorderLayout.SOUTH);
        this.add(centerPanel, BorderLayout.CENTER);
    }

    private void loadTimeSlotsButtonListener() {
        if (!datePicker.isTextFieldValid()) {
            Message.optionPaneError("Please enter a date", this);
            return;
        }
        LocalDate date = datePicker.getDate();
        calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        roomMap = gui.getClient().getAvailableTimeSlot(calendar, gui.getCampusOfTheID());
        if (roomMap == null || roomMap.size() == 0)
            Message.optionPaneError("No room has been created on " + calendar.getTime(), this);
        else {
            //reset combo box
            roomComboBox.removeAllItems();

            //load combo box
            //do not need to load table, loading combo box will load table by the combo box listener
            for (Map.Entry<String, Room> entry : roomMap.entrySet())
                roomComboBox.addItem(new RoomComboItem(entry.getValue()));
        }
    }

    private void roomComboBoxListener() {
        removeAllRows(deleteTableModel);
        removeAllRows(currentTableModel);
        Room room;
        if (roomComboBox.getItemCount() != 0) {
            room = ((RoomComboItem) roomComboBox.getSelectedItem()).getRoom();
            Vector<Object> row;
            for (TimeSlot slot : room.getTimeSlots()) {
                row = new Vector<>();
                int hour1 = slot.getStartTime().get(Calendar.HOUR_OF_DAY);
                row.add(hour1 + ":" + slot.getStartTime().get(Calendar.MINUTE));
                int hour2 = slot.getEndTime().get(Calendar.HOUR_OF_DAY);
                row.add(hour2 + ":" + slot.getEndTime().get(Calendar.MINUTE));
                row.add(slot.getStudentID() != null);
                currentTableModel.addRow(row);
            }
        }
    }

    private void toRightButtonListener() {
        int selectedRow = currentTable.getSelectedRow();
        if (selectedRow > -1) {
            Vector<Object> get = (Vector<Object>) currentTableModel.getDataVector().get(selectedRow);
            deleteTableModel.addRow(get);
        } else Message.optionPaneError("Empty List", this);
    }

    private void toLeftButtonListener() {
        int selectedRow = deleteTable.getSelectedRow();
        if (selectedRow > -1) deleteTableModel.removeRow(selectedRow);
        else Message.optionPaneError("Empty to be deleted time slot list", this);
    }

    private void deleteSelectedButtonListener() {
        Vector<Vector<Object>> list = (Vector<Vector<Object>>) deleteTableModel.getDataVector();
        if (list.size() == 0) Message.optionPaneError("Please Select Time Slots to be deleted", this);
        else {
            String room = ((RoomComboItem) roomComboBox.getSelectedItem()).getRoom().getRoomNumber();
            java.util.List<TimeSlot> slotList = new LinkedList<>();

            Calendar c1, c2;
            String[] time1, time2;
            int hour1, hour2, min1, min2;
            for (Vector<Object> vector : list) {
                time1 = ((String) (vector.get(0))).split(":");
                hour1 = Integer.parseInt(time1[0]);
                min1 = Integer.parseInt(time1[1]);
                c1 = (Calendar) calendar.clone();
                c1.set(Calendar.HOUR, hour1);
                c1.set(Calendar.MINUTE, min1);

                time2 = ((String) (vector.get(1))).split(":");
                hour2 = Integer.parseInt(time2[0]);
                min2 = Integer.parseInt(time2[1]);
                c2 = (Calendar) calendar.clone();
                c2.set(Calendar.HOUR, hour2);
                c2.set(Calendar.MINUTE, min2);

                slotList.add(new TimeSlot(c1, c2));
            }

            if (gui.getClient().deleteRoom(room, calendar, slotList)) {
                Message.optionPanePlain(
                        "Selected rooms have been removed from " + gui.getCampusOfTheID().name,
                        this);
            } else {
                Message.optionPaneError(
                        "Selected rooms cannot be removed, Please check server log for details",
                        this);
            }
            loadTimeSlotsButton.doClick();

        }
    }

    private void removeAllRows(DefaultTableModel model) {
        for (int i = model.getRowCount() - 1; i > -1; --i)
            model.removeRow(i);
    }

    @RequiredArgsConstructor
    @Getter
    private class RoomComboItem {
        private final Room room;

        public String toString() {
            return room.getRoomNumber();
        }
    }
}
