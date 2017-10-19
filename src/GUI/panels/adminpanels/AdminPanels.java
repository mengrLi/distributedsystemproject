package GUI.panels.adminpanels;


import GUI.functions.HelperFunctions;
import GUI.panels.MainMenuPanel;
import GUI.panels.UserTerminalGUI;

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

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        adminMenuPanel = new AdminMenuPanel(this, gui, "menu");
        createRoomPanel = new CreateRoomPanel(this, gui, "create");
        deleteRoomPanel = new DeleteRoomPanel(this, gui, "delete");

        cardLayout.show(this, "menu");

        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());
    }

}
