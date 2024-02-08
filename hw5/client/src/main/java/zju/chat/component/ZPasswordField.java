package zju.chat.component;

import zju.chat.Style;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import static zju.chat.Style.BORDER_RADIUS;

/**
 * ZPasswordField: the custom password field of the program
 */
public class ZPasswordField extends JPasswordField {

    /**
     * The border color of the password field.
     */
    private final Color borderColor = new Color(222, 226, 230);

    /**
     * Constructor
     */
    public ZPasswordField() {
        super();
        setFont(Style.font);
        setOpaque(false);
        setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    /**
     * Constructor
     *
     * @param text the text of the password field
     */
    public ZPasswordField(String text) {
        super(text);
        setFont(Style.font);
    }

    /**
     * paint the component
     *
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw the background
        g2d.setColor(getBackground());
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS));

        super.paintComponent(g);
        g2d.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw the border
        g2d.setColor(borderColor);
        g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, BORDER_RADIUS, BORDER_RADIUS));

        g2d.dispose();
    }

}
