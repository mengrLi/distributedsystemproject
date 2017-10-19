package GUI.panels.studentpanels;

import GUI.functions.HelperFunctions;
import GUI.panels.UserTerminalGUI;

import javax.swing.*;
import java.awt.*;

public class BookRoomPanel extends JPanel {
    private final StudentPanels parentPanel;
    private final UserTerminalGUI gui;

    public BookRoomPanel(StudentPanels parentPanel, UserTerminalGUI gui, String cardName) {
        this.parentPanel = parentPanel;
        this.gui = gui;
        this.parentPanel.add(this, cardName);

        setLayout(new BorderLayout());
        add(new JLabel("Book room"), BorderLayout.NORTH);
        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());
    }
}
