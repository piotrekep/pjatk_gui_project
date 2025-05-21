package visual;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteCellType {
    public enum Type {
        EMPTY(null,               Color.WHITE),
        WALL("sprites/wall.png",       Color.DARK_GRAY),
        PLAYER("sprites/player.png",     Color.BLUE),
        NPC_CHASER("sprites/npc_chaser.png", Color.GREEN),
        NPC_AGGRO("sprites/npc_aggro.png",  Color.YELLOW),
        NPC_KEYBOARDWARRIOR("sprites/npc_keyboardwarrior.png", Color.MAGENTA),
        NPC_HEADLESSCHICKEN("sprites/npc_headlesschicken.png", Color.CYAN),
        NPC_COWARD("sprites/npc_coward.png", Color.ORANGE),
        GHOSTHOUSE("sprites/ghosthouse.png", Color.GRAY),
        GHOSTFLOOR("sprites/ghostfloor.png", Color.PINK),
        POINT("sprites/point.png",      Color.LIGHT_GRAY),
        POWERUP_LIFE("sprites/powerup_life.png",    Color.RED),
        POWERUP_PEARL("sprites/powerup_pearl.png",    Color.RED),
        POWERUP_POINTS("sprites/powerup_points.png",    Color.RED),
        POWERUP_SPEED("sprites/powerup_speed.png",    Color.RED),
        POWERUP_POOP("sprites/powerup_poop.png",    Color.RED),
        POWERUP("sprites/powerup.png",    Color.RED);

  
        private final String path;
        private final Color placeholderColor;
        private Image sprite;
    
        Type(String path, Color placeholderColor) {
            this.path = path;
            this.placeholderColor = placeholderColor;
            this.sprite = loadSprite(path, placeholderColor);
        }
    

        private static Image loadSprite(String path, Color placeholderColor) {
            try {
                // szukamy zasobu zaczynając od katalogu klasy (root classpath)
                java.net.URL resource = SpriteCellType.class.getResource("/" + path);
                if (resource != null) {
                    return ImageIO.read(resource);
                }
            } catch (IOException e) {
                System.err.println("Błąd podczas wczytywania " + path + ": " + e);
            }
            System.err.println("Nie znaleziono zasobu na classpath: " + path);
            return createPlaceholder(placeholderColor);
        }


        private static Image createPlaceholder(Color color) {
            int size = 32;
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setColor(color);
            g2.fillRect(0, 0, size, size);
            g2.setColor(Color.BLACK);
            g2.drawRect(0, 0, size-1, size-1);
            g2.dispose();
            return img;
        }

        public Image getSprite() {
            return sprite;
        }
    }

    public final Type type;
    public final int  val;

    public SpriteCellType(Type type) {
        this(type, 0);
    }

    public SpriteCellType(Type type, int val) {
        this.type = type;
        this.val  = val;
    }
}