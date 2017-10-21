package GUI.functions;
import java.awt.*;

public class HelperFunctions {

    public static void setDimension(Component component, int width, int height) {
        Dimension dimension = new Dimension(width, height);
        component.setPreferredSize(dimension);
        component.setMinimumSize(dimension);
        component.setMaximumSize(dimension);
    }
}
