package GUI.functions;

import javax.swing.*;
import java.awt.*;

public class HelperFunctions {
    public static void switchPanels(JPanel open, JPanel close) {
        open.setVisible(true);
        close.setVisible(false);
    }

    public static void setDimension(Component component, int width, int height) {
        Dimension dimension = new Dimension(width, height);
        component.setPreferredSize(dimension);
        component.setMinimumSize(dimension);
        component.setMaximumSize(dimension);
    }
}
