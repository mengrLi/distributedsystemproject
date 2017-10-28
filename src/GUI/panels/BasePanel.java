package GUI.panels;

import GUI.UserTerminalGUI;
import GUI.functions.HelperFunctions;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

@Getter
@Setter
public class BasePanel extends JPanel {
    private final JPanel loginPanel;
    private final UserTerminalGUI gui;
    private final MainMenuPanel mainMenuPanel;
    private final CardLayout cardLayout;

    public BasePanel(UserTerminalGUI gui) {
        this.gui = gui;
        HelperFunctions.setDimension(this, 600, 500);
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        loginPanel = new LoginPanel(this, gui, "login");
        mainMenuPanel = new MainMenuPanel(this, gui, "menu");
    }
}
