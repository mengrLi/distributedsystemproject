package GUI.panels.adminpanels;

import GUI.UserTerminalGUI;
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
        this.setLayout(new BorderLayout());
        adminPanels.add(this, cardName);

        JLabel title = new JLabel("Administrator functions");
        title.setFont(new Font("Serif", Font.BOLD, 20 ));
        this.add(title, BorderLayout.NORTH);


        buttonPanel = new JPanel(new GridLayout(3, 1));
        this.add(buttonPanel, BorderLayout.SOUTH);

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
        createRoomButton.addActionListener(e->((CardLayout)parentPanel.getLayout()).show(parentPanel, "create"));
        buttonPanel.add(createRoomButton, index);
    }

    private void setDeleteRoomPanel(int index) {
        deleteRoomButton.addActionListener(e->((CardLayout)parentPanel.getLayout()).show(parentPanel, "delete"));
        buttonPanel.add(deleteRoomButton, index);
    }

    private void setBackButton(int index) {
        backButton.addActionListener(e -> gui.getBasePanel().getCardLayout().show(gui.getBasePanel(), "login"));
        buttonPanel.add(backButton, index);
    }
}
