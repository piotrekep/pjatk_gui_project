package visual;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;

public class MyScrollPane extends JScrollPane {
    
    public MyScrollPane(Component view) {
        super(view);
        getVerticalScrollBar().setUI(new CustomScrollBarUI());
        getVerticalScrollBar().setOpaque(false);
        getHorizontalScrollBar().setOpaque(false);
        //setBorder(null);
    }
    
    private static class CustomScrollBarUI extends BasicScrollBarUI {

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            
        }
 
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            try {
                g.drawImage(ImageIO.read(new File("images/X_EXIT_button.png")), 
                           thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, null);
            } catch (Exception e) {
                g.setColor(Color.GRAY);
                g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
            }
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createArrowButton("images/szczauka_przesuwnik_gora.png");
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createArrowButton("images/szczauka_przesuwnik_dol.png");
        }
        
        private JButton createArrowButton(String imagePath) {
            return new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    try {
                        g.drawImage(ImageIO.read(new File(imagePath)), 0, 0, getWidth(), getHeight(), null);
                    } catch (Exception e) {
                        super.paintComponent(g);
                    }
                }
            };
        }
    }
}