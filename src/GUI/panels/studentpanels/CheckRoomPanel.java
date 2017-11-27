package GUI.panels.studentpanels;

import GUI.UserTerminalGUI;
import GUI.functions.HelperFunctions;
import com.github.lgooddatepicker.components.DatePicker;
import domain.Campus;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Map;

class CheckRoomPanel extends JPanel {
    private final StudentPanels parentPanel;
    private final UserTerminalGUI gui;

    private final JPanel topPanel;
    //    private final JComboBox<CampusBoxItem> campusBox;
    private final DatePicker datePicker;
    private final JButton refreshButton;

    private final JTextField resultField;
    private final JButton backButton;

    private Calendar calendar;


    CheckRoomPanel(StudentPanels parentPanel, UserTerminalGUI gui, String cardName) {
        this.parentPanel = parentPanel;
        this.gui = gui;
        this.parentPanel.add(this, cardName);
        add(new JLabel("check room"), BorderLayout.NORTH);
        setLayout(new BorderLayout());
        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());


        topPanel = new JPanel(new BorderLayout());
//        campusBox = new JComboBox<>();
//        setCampusComboBox();
//        topPanel.addRmResponseToInboundMessage(campusBox, BorderLayout.WEST);

        datePicker = new DatePicker();
        datePicker.setDateToToday();
        LocalDate date = datePicker.getDate();
        calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        topPanel.add(datePicker, BorderLayout.CENTER);

        resultField = new JTextField();

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e->{
            LocalDate date1 = datePicker.getDate();
            calendar.set(date1.getYear(), date1.getMonthValue() - 1, date1.getDayOfMonth(),
                    0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Map<Campus, Integer> get = gui.getClient().getAvailableTimeSlot(calendar);
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<Campus, Integer> entry : get.entrySet())
                builder.append(entry.getKey().name).append(": ").append(entry.getValue()).append(System.lineSeparator());
            resultField.setText(builder.toString());
        });
        topPanel.add(refreshButton, BorderLayout.EAST);
        backButton = new JButton("Back");
        backButton.addActionListener(e->((CardLayout) parentPanel.getLayout()).show(parentPanel, "menu"));

        this.add(topPanel, BorderLayout.NORTH);
        this.add(resultField, BorderLayout.CENTER);
        this.add(backButton, BorderLayout.SOUTH);
    }

//    private void setCampusComboBox(){
//        campusBox.addItem(new CampusBoxItem(Campus.DORVAL));
//        campusBox.addItem(new CampusBoxItem(Campus.KIRKLAND));
//        campusBox.addItem(new CampusBoxItem(Campus.WESTMOUNT));
//
//    }

//    @RequiredArgsConstructor
//    @Getter
//    class CampusBoxItem{
//        final Campus campus;
//
//        public String toString(){
//            return campus.name;
//        }
//    }
}
