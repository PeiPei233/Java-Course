package zju.chat;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.Objects;

/**
 * Style: the style of the program
 */
public class Style {

    /**
     * The border radius of the rectangle.
     */
    public static final int BORDER_RADIUS = 10;

    /**
     * The font of the program.
     */
    public static Font font;

    /**
     * The style of the message in the message display area.
     */
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

            // the style of the user info when sending a message
            fromMeInfoStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(fromMeInfoStyle, Color.GREEN.darker().darker());
            StyleConstants.setFontFamily(fromMeInfoStyle, Style.font.getFamily());
            StyleConstants.setFontSize(fromMeInfoStyle, Style.font.getSize());
            StyleConstants.setAlignment(fromMeInfoStyle, StyleConstants.ALIGN_RIGHT);

            // the style of the user info when receiving a message
            fromOtherInfoStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(fromOtherInfoStyle, Color.BLUE);
            StyleConstants.setFontFamily(fromOtherInfoStyle, Style.font.getFamily());
            StyleConstants.setFontSize(fromOtherInfoStyle, Style.font.getSize());

            // the style of the message when sending a message
            fromMeMessageStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(fromMeMessageStyle, Color.BLACK);
            StyleConstants.setFontFamily(fromMeMessageStyle, Style.font.getFamily());
            StyleConstants.setFontSize(fromMeMessageStyle, Style.font.getSize());
            StyleConstants.setAlignment(fromMeMessageStyle, StyleConstants.ALIGN_RIGHT);

            // the style of the message when receiving a message
            fromOtherMessageStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(fromOtherMessageStyle, Color.BLACK);
            StyleConstants.setFontFamily(fromOtherMessageStyle, Style.font.getFamily());
            StyleConstants.setFontSize(fromOtherMessageStyle, Style.font.getSize());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
