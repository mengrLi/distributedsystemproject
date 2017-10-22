package GUI.panels;

import domain.CampusName;
import lombok.Getter;
import lombok.Setter;
import service.user.UserInterface;

import javax.swing.*;

@Getter
@Setter
public class UserTerminalGUI {
    /**
     * Base panel
     */
    private BasePanel basePanel;
    /**
     * Client interface, student or admin
     */
    private UserInterface client;

    /**
     * boolean is admin
     */
    private boolean admin;

    /**
     * the campus at which student or admin is bound to
     */
    private CampusName campusOfTheID = null;
    /**
     * the campus that the student is trying to connect to
     */
    private CampusName campusOfInterest = null;

    /**
     * student or admin id
     */
    private int id;

    private String fullID;

    public UserTerminalGUI() {
        init();
    }

    private void init() {
        basePanel = new BasePanel(this);

        JFrame frame = new JFrame("GUI");
        frame.setContentPane(basePanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new UserTerminalGUI();
    }

}
