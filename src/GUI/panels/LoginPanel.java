package GUI.panels;

import GUI.functions.HelperFunctions;
import GUI.functions.Message;
import domain.CampusName;
import service.user.AdminClient;
import service.user.StudentClient;
import service.user.UserInterface;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("Duplicates")
public class LoginPanel extends JPanel {
    private final UserTerminalGUI gui;
    private final BasePanel parentPanel;
    private final JTextField usernameField;
    private final JLabel titleLable;
    private final JButton submitButton;

    public LoginPanel(BasePanel parentPanel, UserTerminalGUI gui, String cardName) {
        this.gui = gui;
        this.parentPanel = parentPanel;
        titleLable = new JLabel("Campus Booking System");
        usernameField = new JTextField();
        submitButton = setSubmitButton();
        parentPanel.add(this, cardName);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        HelperFunctions.setDimension(this, parentPanel.getWidth(), parentPanel.getHeight());

        titleLable.setFont(new Font("Serif", Font.BOLD, 40));
        add(titleLable, BorderLayout.NORTH);

        JPanel midPanel = new JPanel();
        HelperFunctions.setDimension(midPanel, 600, 200);
        add(midPanel, BorderLayout.CENTER);

        JPanel inputFieldsContainerPanel = new JPanel(new BorderLayout());
        HelperFunctions.setDimension(inputFieldsContainerPanel, 200, 150);
        add(inputFieldsContainerPanel, BorderLayout.SOUTH);

        JLabel usernameLabel = new JLabel("Username : ");
        usernameLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        inputFieldsContainerPanel.add(usernameLabel, BorderLayout.NORTH);

        usernameField.setFont(new Font("Serif", Font.ITALIC, 20));
        HelperFunctions.setDimension(usernameLabel, 200, 50);
        inputFieldsContainerPanel.add(usernameField, BorderLayout.CENTER);

        HelperFunctions.setDimension(submitButton, 200, 50);
        inputFieldsContainerPanel.add(submitButton, BorderLayout.SOUTH);

    }


    private JButton setSubmitButton() {
        JButton button = new JButton("Log in");
        button.addActionListener(e -> {
            String username = usernameField.getText();
            if(checkUsername(username)){
                CardLayout parentLayout = (CardLayout) getParent().getLayout();
                parentLayout.show(getParent(), "menu");
                MainMenuPanel mainMenuPanel = gui.getBasePanel().getMainMenuPanel();
                mainMenuPanel.loadPanel();
            }
        });
        return button;
    }

    private boolean checkUsername(String username) {
        int id;
        UserInterface client;
        if (username.equals("")) {
            Message.optionPaneError("Empty username", gui.getBasePanel());
            return false;
        }
        if(username.length() != 8){
            Message.optionPaneError("Invalid username - invalid length", gui.getBasePanel());
            return false;
        }

        String type = username.substring(3, 4).toUpperCase();
        String campus = username.substring(0, 3).toUpperCase();

        try {
            id = Integer.parseInt(username.substring(4));
            gui.setId(id);
        } catch (NumberFormatException e) {
            Message.optionPaneError("Invalid username - Number format invalid", gui.getBasePanel());
            return false;
        }
        CampusName campusOfTheID = CampusName.getCampusName(campus);
        if(campusOfTheID == null){
            Message.optionPaneError("Invalid username - Campus name invalid", gui.getBasePanel());
            return false;
        }
        gui.setCampusOfTheID(campusOfTheID);

        if (type.equals("A")) {
            client = new AdminClient(campusOfTheID, id);
            boolean isAdmin = client.checkID();
            if (isAdmin) {
                gui.setClient(client);
                gui.setAdmin(true);
                gui.setFullID(username);
                return true;
            }else{
                Message.optionPaneError("Invalid Admin username", gui.getBasePanel());
                return false;
            }
        } else if (type.equals("S")) {
            client = new StudentClient(campusOfTheID, id);
            gui.setClient(client);
            gui.setAdmin(false);
            gui.setFullID(username);
            return true;
        }else{
            Message.optionPaneError("Invalid username - type indentifier", gui.getBasePanel());
            return false;
        }
    }

}
