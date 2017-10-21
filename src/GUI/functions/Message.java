package GUI.functions;

import javax.swing.*;

public class Message {
    public static void optionPanePlain(String msg, JPanel parent) {
        JOptionPane.showMessageDialog(parent, msg, "", JOptionPane.PLAIN_MESSAGE);
    }

    public static void optionPaneError(String msg, JPanel parent) {
        JOptionPane.showMessageDialog(parent, msg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}
