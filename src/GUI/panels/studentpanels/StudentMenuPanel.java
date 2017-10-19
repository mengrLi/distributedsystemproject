package GUI.panels.studentpanels;

import GUI.functions.HelperFunctions;
import GUI.panels.BasePanel;
import GUI.panels.UserTerminalGUI;
import GUI.panels.buttons.MenuButtons;

import javax.swing.*;
import java.awt.*;

public class StudentMenuPanel extends JPanel {
    private final JButton bookRoomButton;
    private final JButton cancelRoomButton;
    private final JButton checkRoomButton;
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
        this.backButton = new MenuButtons("Back");
        add(new JLabel("Student functions"), BorderLayout.NORTH);
        this.buttonPanel = new JPanel(new GridLayout(4, 1));
        add(buttonPanel, BorderLayout.SOUTH);
        init();
    }

    private void init() {
        setCheckRoomButton(0);
        setBookRoomButton(1);
        setCancelRoomButton(2);
        setBackButton(3);
    }

    private void setCheckRoomButton(int index) {
        checkRoomButton.addActionListener(e -> {
            JPanel parent = (JPanel) getParent();
            ((CardLayout) parent.getLayout()).show(parent, "check");
        });
        buttonPanel.add(checkRoomButton, index);
    }

    private void setBookRoomButton(int index) {
        bookRoomButton.addActionListener(e -> {
            JPanel parent = (JPanel) getParent();
            ((CardLayout) parent.getLayout()).show(parent, "book");
        });

        buttonPanel.add(bookRoomButton, index);
    }

    private void setCancelRoomButton(int index) {
        cancelRoomButton.addActionListener(e -> {
            JPanel parent = (JPanel) getParent();
            ((CardLayout) parent.getLayout()).show(parent, "cancel");
        });
        buttonPanel.add(cancelRoomButton, index);
    }

    private void setBackButton(int index) {
        backButton.addActionListener(e -> {
            BasePanel base = gui.getBasePanel();
            base.getCardLayout().show(base, "login");
        });
        buttonPanel.add(backButton, index);
    }
}
