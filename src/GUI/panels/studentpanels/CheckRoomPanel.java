package GUI.panels.studentpanels;

import GUI.functions.HelperFunctions;
import GUI.panels.UserTerminalGUI;

import javax.swing.*;
import java.awt.*;

public class CheckRoomPanel extends JPanel {
    private final StudentPanels parentPanel;
    private final UserTerminalGUI gui;

    public CheckRoomPanel(StudentPanels parentPanel, UserTerminalGUI gui, String cardName) {
        this.parentPanel = parentPanel;
        this.gui = gui;
        this.parentPanel.add(this, cardName);
        add(new JLabel("check room"), BorderLayout.NORTH);
        setLayout(new BorderLayout());
        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());
    }
}
