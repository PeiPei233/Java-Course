package zju.chat.component;

import zju.chat.Style;

import javax.swing.*;
import java.awt.*;

import static zju.chat.Style.BORDER_RADIUS;

public class ZButton extends JButton {

    private Color borderColor = null;

    public static enum Type {
        PRIMARY, SECONDARY, SUCCESS, DANGER, WARNING, INFO, LIGHT, DARK, LINK
    }

    public ZButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(Style.font);

        setForeground(Color.BLACK);
        setBackground(Color.WHITE);
        borderColor = new Color(222, 226, 230);
    }

    public ZButton(String text, Type type) {
        this(text);
        switch (type) {
            case PRIMARY:
                setForeground(new Color(255, 255, 255));
                setBackground(new Color(22, 119, 255));
                borderColor = null;
                break;
            case SECONDARY:
                setForeground(new Color(255, 255, 255));
                setBackground(new Color(108, 117, 125));
                borderColor = null;
                break;
            case SUCCESS:
                setForeground(new Color(255, 255, 255));
                setBackground(new Color(40, 167, 69));
                borderColor = null;
                break;
            case DANGER:
                setForeground(new Color(255, 255, 255));
                setBackground(new Color(220, 53, 69));
                borderColor = null;
                break;
            case WARNING:
                setForeground(new Color(255, 255, 255));
                setBackground(new Color(255, 193, 7));
                borderColor = null;
                break;
            case INFO:
                setForeground(new Color(255, 255, 255));
                setBackground(new Color(23, 162, 184));
                borderColor = null;
                break;
            case LIGHT:
                setForeground(new Color(0, 0, 0));
                setBackground(new Color(248, 249, 250));
                borderColor = new Color(222, 226, 230);
                break;
            case DARK:
                setForeground(new Color(255, 255, 255));
                setBackground(new Color(52, 58, 64));
                borderColor = null;
                break;
            case LINK:
                setForeground(new Color(0, 123, 255));
                setBackground(new Color(255, 255, 255));
                borderColor = null;
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        if (getModel().isPressed()) {
            g2d.setColor(getBackground().darker());
        } else if (getModel().isRollover()) {
            g2d.setColor(getBackground().brighter());
        } else {
            g2d.setColor(getBackground());
        }
        // draw the background
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS);

        // draw the border
        if (borderColor != null) {
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS);
        }

        g2d.dispose();
        super.paintComponent(g);
    }

}
