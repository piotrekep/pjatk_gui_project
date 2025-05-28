package visual;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * @class BackgroundJbutton
 * @brief Klasa tworząca przycisk z obrazem
 */

public class BackgroundJbutton extends JButton {
    private BufferedImage backgroundImage;
    
    public BackgroundJbutton() {
        super();
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
    }

    
    public BackgroundJbutton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
    }   
    
    public BackgroundJbutton(String text, String imagePath) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBackgroundImage(imagePath);
    }
    
    private void setBackgroundImage(String imagePath) {
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
            repaint();
        } catch (IOException e) {
            
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        super.paintComponent(g);
    }
}