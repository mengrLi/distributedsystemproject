package GUI.panels.buttons;

import GUI.functions.HelperFunctions;

import javax.swing.*;

public class MenuButtons extends JButton {
    public MenuButtons(String text) {
        HelperFunctions.setDimension(this, 200, 80);
        setText(text);
    }
}
