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

        studentMenuPanel = new StudentPanels(this, gui, "student");
        adminMenuPanel = new AdminPanels(this, gui, "admin");
    }

    void loadPanel(){
        cardLayout.show(this, gui.isAdmin() ? "admin" : "student");

        //guarantee to show the menu card
        if(gui.isAdmin()) ((CardLayout) adminMenuPanel.getLayout()).show(adminMenuPanel, "menu");
        else ((CardLayout) studentMenuPanel.getLayout()).show(studentMenuPanel, "menu");

    }


}
