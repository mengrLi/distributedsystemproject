package GUI.panels.adminpanels;


import GUI.UserTerminalGUI;
import GUI.functions.HelperFunctions;
import GUI.panels.MainMenuPanel;

import javax.swing.*;
import java.awt.*;

public class AdminPanels extends JPanel {
    private final MainMenuPanel parentPanel;
    private final UserTerminalGUI gui;

    private final JPanel createRoomPanel;
    private final JPanel deleteRoomPanel;
    private final JPanel adminMenuPanel;

    private final CardLayout cardLayout;

    public AdminPanels(MainMenuPanel parentPanel, UserTerminalGUI gui, String cardName) {
        this.parentPanel = parentPanel;
        this.gui = gui;
        this.parentPanel.add(this, cardName);
        this.cardLayout = new CardLayout();
        this.setLayout(cardLayout);
        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());

        this.adminMenuPanel = new AdminMenuPanel(this, gui, "menu");
        this.createRoomPanel = new CreateRoomPanel(this, gui, "create");
        this.deleteRoomPanel = new DeleteRoomPanel(this, gui, "delete");
    }

}
