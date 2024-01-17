package zju.chat.component;

import zju.chat.Style;

import javax.swing.*;
import java.awt.*;

public class ZLabel extends JLabel {

    public static enum Type {
        PRIMARY, SECONDARY, SUCCESS, DANGER, WARNING, INFO, LIGHT, DARK, LINK
    }

    public ZLabel(String text) {
        super(text);
        setFont(Style.font);
    }

    public ZLabel(String text, Type type) {
        this(text);
        switch (type) {
            case PRIMARY:
                setForeground(new Color(22, 119, 255));
                break;
            case SECONDARY:
                setForeground(new Color(108, 117, 125));
                break;
            case SUCCESS:
                setForeground(new Color(40, 167, 69));
                break;
            case DANGER:
                setForeground(new Color(220, 53, 69));
                break;
            case WARNING:
                setForeground(new Color(255, 193, 7));
                break;
            case INFO:
                setForeground(new Color(23, 162, 184));
                break;
            case LIGHT:
                setForeground(new Color(248, 249, 250));
                break;
            case DARK:
                setForeground(new Color(52, 58, 64));
                break;
            case LINK:
                setForeground(new Color(0, 123, 255));
                break;
        }
    }

}
