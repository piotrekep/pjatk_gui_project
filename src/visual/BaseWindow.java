package visual;

import javax.swing.*;
import java.awt.*;

/**
 * @class BaseWindow
 * @brief Klasa tworząca spójną kolorystykę wszystkich okien
 */
public class BaseWindow extends JFrame {

    protected static final Color BACKGROUND_COLOR = new Color(0x2D2D30);
    protected static final Color FOREGROUND_COLOR = new Color(0xFFFFFF);
    protected static final Color LABEL_FOREGROUND_COLOR = new Color(255, 207, 38);
    protected static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 22);
    protected static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 22);

    public BaseWindow(String title) {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        UIManager.put("Label.font", FONT_BOLD);
        UIManager.put("Label.foreground", LABEL_FOREGROUND_COLOR);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("Button.foreground", FOREGROUND_COLOR);
        UIManager.put("Button.background", new Color(0x007ACC));
        setLocationRelativeTo(null);
    }

    protected void centerAndShow(int width, int height) {
        setSize(width, height);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}