package zju.chat;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.Objects;

public class Style {

    public static final int BORDER_RADIUS = 10;
    public static Font font;
    public static SimpleAttributeSet fromMeInfoStyle;
    public static SimpleAttributeSet fromOtherInfoStyle;
    public static SimpleAttributeSet fromMeMessageStyle;
    public static SimpleAttributeSet fromOtherMessageStyle;

    static {

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Main.class.getResourceAsStream("/NotoSansSC-Regular.ttf")));
            font = font.deriveFont(Font.PLAIN, 14);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // register the font
            ge.registerFont(font);

            fromMeInfoStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(fromMeInfoStyle, Color.GREEN.darker().darker());
            StyleConstants.setFontFamily(fromMeInfoStyle, Style.font.getFamily());
            StyleConstants.setFontSize(fromMeInfoStyle, Style.font.getSize());
            StyleConstants.setAlignment(fromMeInfoStyle, StyleConstants.ALIGN_RIGHT);

            fromOtherInfoStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(fromOtherInfoStyle, Color.BLUE);
            StyleConstants.setFontFamily(fromOtherInfoStyle, Style.font.getFamily());
            StyleConstants.setFontSize(fromOtherInfoStyle, Style.font.getSize());

            fromMeMessageStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(fromMeMessageStyle, Color.BLACK);
            StyleConstants.setFontFamily(fromMeMessageStyle, Style.font.getFamily());
            StyleConstants.setFontSize(fromMeMessageStyle, Style.font.getSize());
            StyleConstants.setAlignment(fromMeMessageStyle, StyleConstants.ALIGN_RIGHT);

            fromOtherMessageStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(fromOtherMessageStyle, Color.BLACK);
            StyleConstants.setFontFamily(fromOtherMessageStyle, Style.font.getFamily());
            StyleConstants.setFontSize(fromOtherMessageStyle, Style.font.getSize());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
