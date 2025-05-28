package visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
/**
 * @class BackgroundJpanel
 * @brief klasa tworząca JPanel z obrazem tła
 */
public class BackgroundJpanel extends JPanel {
    private BufferedImage backgroundImage;
    

    public BackgroundJpanel(String imagePath) {
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Nie można załadować obrazu tła: " + imagePath);
            e.printStackTrace();
            //domyślny kolor tła
            setBackground(Color.DARK_GRAY);
        }
    }
    
    public BackgroundJpanel(GridLayout layout, String imagePath){
        super(layout);
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Nie można załadować obrazu tła: " + imagePath);
            e.printStackTrace();
            //domyślny kolor tła
            setBackground(Color.DARK_GRAY);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Opcja 1: Rozciągnij obraz na cały panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            
        }
    }
}