package zju.chat.component;

import javax.swing.*;
import java.awt.*;

public class ZDialog extends JDialog {
    public ZDialog(Frame parent, String message, String title, int messageType) {
        super(parent, title, true);
        setLocationRelativeTo(parent);
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
