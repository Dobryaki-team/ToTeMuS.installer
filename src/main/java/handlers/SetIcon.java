package handlers;

import javax.swing.*;

public class SetIcon {
    private static JFrame frame;

    public static void set(final JFrame frame) {
        SetIcon.frame = frame;
        update();
    }

    private static void update() {
        ImageIcon icon = new ImageIcon("/icons/temp_icon.ico");
        frame.setIconImage(icon.getImage());
    }
}
