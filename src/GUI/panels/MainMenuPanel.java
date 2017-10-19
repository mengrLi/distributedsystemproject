package GUI.panels;

import GUI.functions.HelperFunctions;
import GUI.panels.adminpanels.AdminPanels;
import GUI.panels.studentpanels.StudentPanels;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class MainMenuPanel extends JPanel {
    private final UserTerminalGUI gui;
    private final BasePanel parentPanel;
    private final CardLayout cardLayout;

    private final StudentPanels studentMenuPanel;
    private final AdminPanels adminMenuPanel;


    public MainMenuPanel(BasePanel parentPanel, UserTerminalGUI gui, String cardName) {
        this.parentPanel = parentPanel;
        this.gui = gui;
        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        this.parentPanel.add(this, cardName);

        studentMenuPanel = new StudentPanels(this, gui, "admin");
        adminMenuPanel = new AdminPanels(this, gui, "student");
    }

    void loadPanel(boolean isAdmin) {
        cardLayout.show(this, isAdmin ? "admin" : "student");
    }


}
