package GUI.panels.studentpanels;

import GUI.UserTerminalGUI;
import GUI.functions.HelperFunctions;
import GUI.panels.BasePanel;
import GUI.panels.buttons.MenuButtons;

import javax.swing.*;
import java.awt.*;

public class StudentMenuPanel extends JPanel {
    private final JButton bookRoomButton;
    private final JButton cancelRoomButton;
    private final JButton checkRoomButton;
    private final JButton switchRoomButton;
    private final JButton backButton;

    private final JPanel parentPanel;
    private final UserTerminalGUI gui;

    private final JPanel buttonPanel;

    StudentMenuPanel(JPanel parentPanel, UserTerminalGUI gui, String cardName) {
        this.parentPanel = parentPanel;
        this.gui = gui;
        this.parentPanel.add(this, cardName);

        setLayout(new BorderLayout());
        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());

        this.bookRoomButton = new MenuButtons("Book Room");
        this.cancelRoomButton = new MenuButtons("Cancel Booking");
        this.checkRoomButton = new MenuButtons("Check Available Rooms");
        this.switchRoomButton = new MenuButtons("Switch Booked Room");
        this.backButton = new MenuButtons("Back");
        JLabel title = new JLabel("Student functions");
        title.setFont(new Font("Serif", Font.BOLD, 20 ));
        add(title, BorderLayout.NORTH);
        this.buttonPanel = new JPanel(new GridLayout(5, 1));
        add(buttonPanel, BorderLayout.SOUTH);
        init();
    }

    private void init() {
        setCheckRoomButton(0);
        setBookRoomButton(1);
        setCancelRoomButton(2);
        setSwitchRoomButton(3);
        setBackButton(4);
    }

    private void setCheckRoomButton(int index) {
        checkRoomButton.addActionListener(e -> switchPanel("check"));
        buttonPanel.add(checkRoomButton, index);
    }

    private void setBookRoomButton(int index) {
        bookRoomButton.addActionListener(e -> switchPanel("book"));
        buttonPanel.add(bookRoomButton, index);
    }

    private void setCancelRoomButton(int index) {
        cancelRoomButton.addActionListener(e -> switchPanel("cancel"));
        buttonPanel.add(cancelRoomButton, index);
    }

    private void setSwitchRoomButton(int index) {
        switchRoomButton.addActionListener(e -> switchPanel("switch"));
        buttonPanel.add(switchRoomButton, index);
    }

    private void switchPanel(String cardName) {
        JPanel parent = (JPanel) getParent();
        ((CardLayout) parent.getLayout()).show(parent, cardName);
    }
    private void setBackButton(int index) {
        backButton.addActionListener(e -> {
            BasePanel base = gui.getBasePanel();
            base.getCardLayout().show(base, "login");
        });
        buttonPanel.add(backButton, index);
    }


}
