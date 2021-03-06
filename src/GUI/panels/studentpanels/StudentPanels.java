package GUI.panels.studentpanels;

import GUI.UserTerminalGUI;
import GUI.functions.HelperFunctions;
import GUI.panels.MainMenuPanel;

import javax.swing.*;
import java.awt.*;

public class StudentPanels extends JPanel {
    private final MainMenuPanel parentPanel;
    private final UserTerminalGUI gui;
    private final CardLayout cardLayout;

    private final BookRoomPanel bookRoomPanel;
    private final CancelRoomPanel cancelRoomPanel;
    private final CheckRoomPanel checkRoomPanel;
    private final StudentMenuPanel studentMenuPanel;
    private final SwitchRoomPanel switchRoomPanel;


    public StudentPanels(MainMenuPanel parentPanel, UserTerminalGUI gui, String cardName) {
        this.parentPanel = parentPanel;
        this.gui = gui;
        this.parentPanel.add(this, cardName);
        this.cardLayout = new CardLayout();
        this.setLayout(cardLayout);
        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());

        this.studentMenuPanel = new StudentMenuPanel(this, gui, "menu");
        this.bookRoomPanel = new BookRoomPanel(this, gui, "book");
        this.cancelRoomPanel = new CancelRoomPanel(this, gui, "cancel");
        this.checkRoomPanel = new CheckRoomPanel(this, gui, "check");
        this.switchRoomPanel = new SwitchRoomPanel(this, gui, "switch");


    }


}
