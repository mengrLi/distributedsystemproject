package GUI.panels.adminpanels;

import GUI.panels.UserTerminalGUI;
import GUI.panels.buttons.MenuButtons;

import javax.swing.*;
import java.awt.*;

public class AdminMenuPanel extends JPanel {
    private final AdminPanels parentPanel;
    private final UserTerminalGUI gui;

    private final JPanel buttonPanel;
    private final JButton createRoomButton, deleteRoomButton, backButton;

    public AdminMenuPanel(AdminPanels adminPanels, UserTerminalGUI gui, String cardName) {
        this.parentPanel = adminPanels;
        this.gui = gui;

        buttonPanel = new JPanel(new GridLayout(3, 1));
        createRoomButton = new MenuButtons("Create Rooms");
        deleteRoomButton = new MenuButtons("Delete Rooms");
        backButton = new MenuButtons("Back");


        init();
    }

    private void init() {
        setCreateRoomButton(0);
        setDeleteRoomPanel(1);
        setBackButton(2);
    }

    private void setCreateRoomButton(int index) {

        buttonPanel.add(createRoomButton, index);
    }

    private void setDeleteRoomPanel(int index) {


        buttonPanel.add(deleteRoomButton, index);
    }

    private void setBackButton(int index) {

        buttonPanel.add(backButton, index);
    }
}
