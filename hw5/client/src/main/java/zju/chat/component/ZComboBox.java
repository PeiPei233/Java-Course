package zju.chat.component;

import zju.chat.Style;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import static zju.chat.Style.BORDER_RADIUS;

public class ZComboBox<E> extends JComboBox<E> {

    private Color borderColor = new Color(222, 226, 230);

    public ZComboBox() {
        super();
        setStyle();
    }

    public ZComboBox(E[] items) {
        super(items);
        setStyle();
    }

    public ZComboBox(ComboBoxModel<E> aModel) {
        super(aModel);
        setStyle();
    }

    public ZComboBox(java.util.Vector<E> items) {
        super(items);
        setStyle();
    }

    private void setStyle() {
        setFont(Style.font);
        setEditable(false);
        setOpaque(false);
        setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw the background
        g2d.setColor(getBackground());
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS));

        // draw the selected text
        g2d.setColor(getForeground());
        g2d.drawString(getSelectedItem().toString(), 10, (getHeight() + getFont().getSize()) / 2);

//        super.paintComponent(g);
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
