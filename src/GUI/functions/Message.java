package GUI.functions;

import javax.swing.*;

/**
 * Created by PT-PC on 2017-10-19.
 */
public class Message {
    public static void optionPanePlain(String msg, JPanel parent) {

    }

    public static void optionPaneError(String msg, JPanel parent) {
        JOptionPane.showMessageDialog(parent, msg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}
