package GUI.panels.adminpanels;

import GUI.functions.Message;
import GUI.panels.UserTerminalGUI;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import domain.TimeSlot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


public class CreateRoomPanel extends JPanel {
    private final AdminPanels parentPanel;
    private final UserTerminalGUI gui;

    private final DatePicker datePicker;
    private final TimePicker timePicker1, timePicker2;
    private final JTextField roomNameField;

    private final JButton addTimeSlotButton, removeSelectedButton, submitButton, backButton;

    private final JTable adminTable;
    private final DefaultTableModel adminTableModel;
    private final Vector<String> header;
//    private List<List<Object>> data;
    private List<TimeSlot> list;

    public CreateRoomPanel(AdminPanels adminPanels, UserTerminalGUI gui, String cardName) {
        this.parentPanel = adminPanels;
        this.gui = gui;
        this.setLayout(new BorderLayout());
        adminPanels.add(this, cardName);

        JLabel title = new JLabel("Create Time Slots");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        this.add(title, BorderLayout.NORTH);

        datePicker = new DatePicker();
        datePicker.setDateToToday();

        timePicker1 = new TimePicker();
        timePicker2 = new TimePicker();
        timePicker1.setTimeToNow();
        timePicker2.setTimeToNow();

        roomNameField = new JTextField();

        addTimeSlotButton = new JButton("Add");
        addTimeSlotButton.addActionListener(e -> addTimeSlotButtonListener());
        removeSelectedButton = new JButton("Remove");
        removeSelectedButton.addActionListener(e->removeTimeSlotButtonListener());
        submitButton = new JButton("Submit");
        submitButton.addActionListener(e->submitButtonListener());
        backButton = new JButton("Back");
        backButton.addActionListener(e->((CardLayout)parentPanel.getLayout()).show(parentPanel, "menu"));


        header = new Vector<>();
        setHeader();
        adminTable = new JTable();
        adminTableModel = new DefaultTableModel(header, 0);
        adminTable.setModel(adminTableModel);

        insertPanels();
    }

    private void setHeader(){
        header.add("Index");
        header.add("Start");
        header.add("End");
    }


    private void insertPanels(){
        JPanel top, bottom;

        JPanel panel1, panel2, panel3, panel4;

        panel1 = new JPanel(new BorderLayout());
        panel2 = new JPanel(new GridLayout(1,2));
        panel3 = new JPanel(new BorderLayout());
        panel4 = new JPanel(new GridLayout(4,1));

        top = new JPanel(new BorderLayout());
        top.add(panel1, BorderLayout.NORTH);
        top.add(panel2, BorderLayout.SOUTH);

        bottom = new JPanel(new BorderLayout());
        bottom.add(panel3, BorderLayout.WEST);
        bottom.add(panel4, BorderLayout.EAST);

        this.add(top, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);

        panel1.add(datePicker, BorderLayout.WEST);
        panel1.add(roomNameField);

        panel2.add(timePicker1);
        panel2.add(timePicker2);

        panel3.add(new JScrollPane(adminTable));

        panel4.add(addTimeSlotButton);
        panel4.add(removeSelectedButton);
        panel4.add(submitButton);
        panel4.add(backButton);
    }

    private void addTimeSlotButtonListener(){
        if (timePicker1.getText().equals("") || timePicker2.getText().equals(""))
            Message.optionPaneError("Please choose a time slot", this);
        else {
            Object[] innerList = new Object[3];
            innerList[0] = adminTableModel.getRowCount() + 1;
            innerList[1] = timePicker1.getTime();
            innerList[2] = timePicker2.getTime();
            adminTableModel.addRow(innerList);
        }
    }

    private void removeTimeSlotButtonListener(){
        if (adminTableModel.getDataVector().size() == 0) Message.optionPaneError("Empty time slot list", this);
        else adminTableModel.removeRow(adminTable.getSelectedRow());
    }

    private void submitButtonListener(){
        if (roomNameField.getText().equals("")) {
            Message.optionPaneError("Please enter a room number", this);
            return;
        }
        if (datePicker.getText().equals("")) {
            Message.optionPaneError("Please choose a date", this);
            return;
        }
        if (adminTableModel.getDataVector().size() == 0) {
            Message.optionPaneError("Please choose a time slot ", this);
            return;
        }
        Calendar calendar = Calendar.getInstance();
        LocalDate date = datePicker.getDate();
        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Vector<Vector<Object>> data = (Vector<Vector<Object>>) adminTableModel.getDataVector();
        Calendar timeSlot1, timeSlot2;
        list = new LinkedList<>();
        for (Vector<Object> vector : data) {
            timeSlot1 = (Calendar) calendar.clone();
            timeSlot1.add(Calendar.HOUR, ((LocalTime) (vector.elementAt(1))).getHour());
            timeSlot1.add(Calendar.MINUTE, ((LocalTime) (vector.elementAt(1))).getMinute());
            timeSlot2 = (Calendar) calendar.clone();
            timeSlot2.add(Calendar.HOUR, ((LocalTime) (vector.elementAt(2))).getHour());
            timeSlot2.add(Calendar.MINUTE, ((LocalTime) (vector.elementAt(2))).getMinute());
            list.add(new TimeSlot(timeSlot1, timeSlot2));
        }

        if (gui.getClient().createRoom(roomNameField.getText(), calendar, list))
            Message.optionPanePlain(
                    list.size() + " time slots have been added to room " +
                            roomNameField.getText() + " in " +
                            gui.getCampusOfTheID().name + " on " +
                            calendar.getTime(),
                    this);
        else Message.optionPaneError("Create room error, please check server log", this);

        //reset the list at the end
        list.clear();
        if (adminTableModel.getRowCount() > 0)
            for (int i = adminTableModel.getRowCount() - 1; i > -1; --i)
                adminTableModel.removeRow(i);
    }
}
