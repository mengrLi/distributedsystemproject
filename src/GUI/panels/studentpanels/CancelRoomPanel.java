package GUI.panels.studentpanels;

import GUI.UserTerminalGUI;
import GUI.functions.HelperFunctions;
import GUI.functions.Message;

import javax.swing.*;
import java.awt.*;

class CancelRoomPanel extends JPanel{
    private final StudentPanels parentPanel;
    private final UserTerminalGUI gui;

    private final JTextField resultField;
    private final JPanel inputPanel;
    private final JTextField bookIdField;
    private final JButton cancelBookingButton;
    private final JButton backButton;


    CancelRoomPanel(StudentPanels parentPanel, UserTerminalGUI gui, String cardName){
        this.parentPanel = parentPanel;
        this.gui = gui;
        this.parentPanel.add(this, cardName);
        this.setLayout(new BorderLayout());
        JLabel title = new JLabel("cancel room");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        this.add(title, BorderLayout.NORTH);
        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());

        resultField = new JTextField();
        HelperFunctions.setDimension(resultField, parentPanel.getWidth(), 200);
        this.add(resultField, BorderLayout.CENTER);

        inputPanel = new JPanel(new GridLayout(4, 1));
        HelperFunctions.setDimension(inputPanel, parentPanel.getWidth(), 200);
        this.add(inputPanel, BorderLayout.SOUTH);

        JLabel text = new JLabel("Enter your booking ID: ");
        text.setFont(new Font("Serif", Font.BOLD, 18));
        inputPanel.add(text, 0);

        bookIdField = new JTextField();
        HelperFunctions.setDimension(bookIdField, parentPanel.getWidth(), 80);
        inputPanel.add(bookIdField, 1);

        cancelBookingButton = new JButton("Cancel Booking");
        HelperFunctions.setDimension(cancelBookingButton, 150, 80);
        inputPanel.add(cancelBookingButton, 2);
        cancelBookingButton.addActionListener(e->setButton());

        backButton = new JButton("Back");
        HelperFunctions.setDimension(backButton, 150, 80);
        inputPanel.add(backButton, 3);
        backButton.addActionListener(e->((CardLayout) parentPanel.getLayout()).show(parentPanel, "menu"));
    }

    private void setButton(){
        String bookingID = bookIdField.getText();
        if(bookingID.equals("")) Message.optionPaneError("Please enter your booking ID", this);
        else{
            if(gui.getClient().cancelBooking(bookingID)) resultField.setText("Booking has been cancelled");
            else resultField.setText("Booking cannot be cancelled");
        }
    }
}
