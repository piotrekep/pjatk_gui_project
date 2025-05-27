package visual;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class SpriteCellType {
    public enum Type {
        EMPTY(new String[] { null }, Color.BLACK),
        WALL(new String[] { "sprites/klocek.png" }, Color.DARK_GRAY),

        PLAYER(new String[] { "sprites/pacan_1.png",
                "sprites/pacan_2.png",
                "sprites/pacan_3.png", }, Color.BLUE),

        // NPC z wieloma klatkami animacji
        NPC_CHASER(new String[] {
                "sprites/czerwony_duch_1.png",
                "sprites/czerwony_duch_2.png",
                "sprites/czerwony_duch_3.png",
                "sprites/czerwony_duch_4.png"
        }, Color.GREEN),

        NPC_AGGRO(new String[] {
                "sprites/fioletowy_duch_1.png",
                "sprites/fioletowy_duch_2.png",
                "sprites/fioletowy_duch_3.png",
                "sprites/fioletowy_duch_4.png"
        }, Color.YELLOW),

        NPC_KEYBOARDWARRIOR(new String[] {
                "sprites/niebieski_duch_1.png",
                "sprites/niebieski_duch_2.png",
                "sprites/niebieski_duch_3.png",
                "sprites/niebieski_duch_4.png"
        }, Color.MAGENTA),

        NPC_HEADLESSCHICKEN(new String[] {
                "sprites/pomaranczowy_duch_1.png",
                "sprites/pomaranczowy_duch_2.png",
                "sprites/pomaranczowy_duch_3.png",
                "sprites/pomaranczowy_duch_4.png"
        }, Color.CYAN),

        NPC_COWARD(new String[] {
                "sprites/zielony_duch_1.png",
                "sprites/zielony_duch_2.png",
                "sprites/zielony_duch_3.png",
                "sprites/zielony_duch_4.png"
        }, Color.ORANGE),

        NPC_POWERUP(new String[] {
                "sprites/spowolniony_duch_1.png",
                "sprites/spowolniony_duch_2.png",
                "sprites/spowolniony_duch_3.png",
                "sprites/spowolniony_duch_4.png"
        }, Color.ORANGE),

        GHOSTHOUSE(new String[] { "sprites/klocek.png" }, Color.GRAY),
        GHOSTFLOOR(new String[] { "sprites/ghostfloor.png" }, Color.PINK),
        POINT(new String[] { "sprites/kulka.png" }, Color.LIGHT_GRAY),

        POWERUP_LIFE(new String[] { "sprites/POWERUP_SERCE.png" }, Color.RED),
        POWERUP_PEARL(new String[] { "sprites/powerup_pearl.png" }, Color.RED),
        POWERUP_POINTS(new String[] { "sprites/POWERUP_+200.png" }, Color.RED),
        POWERUP_SPEED(new String[] { "sprites/POWERUP_SKRZYDLO.png" }, Color.RED),
        POWERUP_POOP(new String[] { "sprites/POWERUP_LOD.png" }, Color.RED),
        POWERUP(new String[] { "sprites/powerup.png" }, Color.RED);

        private final String[] spritePaths;
        private final Color placeholderColor;
        private Image[] sprites;
        private Image[] scaledSprites;


        Type(String[] paths, Color placeholderColor) {
            this.spritePaths = paths;
            this.placeholderColor = placeholderColor;
            this.sprites = loadSprites(paths, placeholderColor);
            this.scaledSprites=this.sprites;
        }

        private static Image[] loadSprites(String[] paths, Color placeholderColor) {
            Image[] images = new Image[paths.length];
            for (int i = 0; i < paths.length; i++) {
                if (paths[i] == null) {
                    images[i] = createPlaceholder(placeholderColor);
                } else {
                    images[i] = loadSprite(paths[i], placeholderColor);
                }
            }
            return images;
        }

        private static Image loadSprite(String path, Color placeholderColor) {
            try {
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
            int size = 64;
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setColor(color);
            g2.fillRect(0, 0, size, size);
            g2.setColor(Color.BLACK);
            g2.drawRect(0, 0, size - 1, size - 1);
            g2.dispose();
            return img;
        }

        public Image getSprite(double animProgress, int direction) {
            if (animProgress < 0.3)
                return getSprite(0, direction);
            else if (animProgress < 0.6)
                return getSprite(1, direction);
            else
                return getSprite(2, direction);

        }

        public Image getSprite(int idx, int direction) {
            Image img;

            if (idx < 0 || idx >= scaledSprites.length) {
                img = scaledSprites[0];
            } else
                img = scaledSprites[idx];

            BufferedImage buffImage = new BufferedImage(
                    img.getWidth(null),
                    img.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = buffImage.createGraphics();

            g.translate(img.getWidth(null) / 2, img.getHeight(null) / 2);

            switch (direction) {
                case 1 -> g.rotate(Math.toRadians(90));
                case 2 -> g.rotate(Math.toRadians(180));
                case 3 -> g.rotate(Math.toRadians(270));
                case 4 -> g.rotate(Math.toRadians(0));
                default -> {
                }
            }

            g.drawImage(img, -img.getWidth(null) / 2, -img.getHeight(null) / 2, null);
            g.dispose();
            return (Image) buffImage;
        }

        public Image getSprite(int idx) {
            if (idx < 0 || idx >= sprites.length) {
                return scaledSprites[0];
            }
            return scaledSprites[idx];
        }

        public Image[] getSprites(){
            return sprites;
        }
        public void rescale(int w, int h){

            Image[] tmp = new Image[sprites.length];
            for (int i = 0; i < sprites.length; i++)
                tmp[i] = scale(sprites[i], w, h);
    
            this.scaledSprites = tmp;
        }
        private static Image scale(Image src, int w, int h) {
            BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = dst.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                               RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(src, 0, 0, w, h, null);
            g.dispose();
            return dst;
        }

    }


    public final Type type;
    public final int val;

    public SpriteCellType(Type type) {
        this(type, 0);
    }

    public SpriteCellType(Type type, int val) {
        this.type = type;
        this.val = val;
    }
}