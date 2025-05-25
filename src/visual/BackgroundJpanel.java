package visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class BackgroundJpanel extends JPanel {
    private BufferedImage backgroundImage;
    

    public BackgroundJpanel(String imagePath) {
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Nie można załadować obrazu tła: " + imagePath);
            e.printStackTrace();
            // Można ustawić domyślny kolor tła jako fallback
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
            // Można ustawić domyślny kolor tła jako fallback
            setBackground(Color.DARK_GRAY);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Opcja 1: Rozciągnij obraz na cały panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            
            // Opcja 2: Zachowaj proporcje obrazu (zakomentuj powyższą linię i odkomentuj poniższe)
            /*
            int imgWidth = backgroundImage.getWidth();
            int imgHeight = backgroundImage.getHeight();
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // Oblicz skalowanie zachowując proporcje
            double scaleX = (double) panelWidth / imgWidth;
            double scaleY = (double) panelHeight / imgHeight;
            double scale = Math.min(scaleX, scaleY);
            
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);
            
            // Wyśrodkuj obraz
            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;
            
            g.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, this);
            */
        }
    }
}