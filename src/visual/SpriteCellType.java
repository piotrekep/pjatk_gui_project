package visual;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.Agent;

/**
 * @class SpriteCellType
 * @brief Klasa obsługująca sprite'y
 */
public class SpriteCellType {

    /**
     * @enum Type
     * @brief Enum definiujący wszystkie dostępne typy sprite'ów w grze
     * 
     *        Każdy typ zawiera ścieżki do plików graficznych, kolor placeholdera
     *        oraz logikę obsługi sprite'ów
     */
    public enum Type {
        /** pusta komórka */
        EMPTY(new String[] { null }, Color.BLACK),
        /** komórka ściany */
        WALL(new String[] { "sprites/klocek.png" }, Color.DARK_GRAY),
        /** komórka gracza z trzema klatkami animacji */
        PLAYER(new String[] { "sprites/pacan_1.png",
                "sprites/pacan_2.png",
                "sprites/pacan_3.png", }, Color.BLUE),
        /** komórka przeciwnika o osobowści "chaser" z animacją */
        NPC_CHASER(new String[] {
                "sprites/czerwony_duch_1.png",
                "sprites/czerwony_duch_2.png",
                "sprites/czerwony_duch_3.png",
                "sprites/czerwony_duch_4.png"
        }, Color.GREEN),
        /** komórka przeciwnika o osobowści "aggro" z animacją */
        NPC_AGGRO(new String[] {
                "sprites/fioletowy_duch_1.png",
                "sprites/fioletowy_duch_2.png",
                "sprites/fioletowy_duch_3.png",
                "sprites/fioletowy_duch_4.png"
        }, Color.YELLOW),
        /** komórka przeciwnika o osobowści "keyboardwarrior" z animacją */
        NPC_KEYBOARDWARRIOR(new String[] {
                "sprites/niebieski_duch_1.png",
                "sprites/niebieski_duch_2.png",
                "sprites/niebieski_duch_3.png",
                "sprites/niebieski_duch_4.png"
        }, Color.MAGENTA),
        /** komórka przeciwnika o osobowści "headlesschicken" z animacją */
        NPC_HEADLESSCHICKEN(new String[] {
                "sprites/pomaranczowy_duch_1.png",
                "sprites/pomaranczowy_duch_2.png",
                "sprites/pomaranczowy_duch_3.png",
                "sprites/pomaranczowy_duch_4.png"
        }, Color.CYAN),
        /** komórka przeciwnika o osobowści "coward" z animacją */
        NPC_COWARD(new String[] {
                "sprites/zielony_duch_1.png",
                "sprites/zielony_duch_2.png",
                "sprites/zielony_duch_3.png",
                "sprites/zielony_duch_4.png"
        }, Color.ORANGE),
        /** komórka przeciwnika pod wpływem gracza z powerupem z animacją */
        NPC_POWERUP(new String[] {
                "sprites/spowolniony_duch_1.png",
                "sprites/spowolniony_duch_2.png",
                "sprites/spowolniony_duch_3.png",
                "sprites/spowolniony_duch_4.png"
        }, Color.ORANGE),
        /** komórka domu duchów */
        GHOSTHOUSE(new String[] { "sprites/klocek.png" }, Color.GRAY),
        /** komórka podłogi domu dhcoów */
        GHOSTFLOOR(new String[] { "sprites/ghostfloor.png" }, Color.PINK),
        /** komórka z punktem */
        POINT(new String[] { "sprites/kulka.png" }, Color.LIGHT_GRAY),
        /** powerup dodatkowe życie */
        POWERUP_LIFE(new String[] { "sprites/POWERUP_SERCE.png" }, Color.RED),
        /** powerup perła */
        POWERUP_PEARL(new String[] { "sprites/powerup_pearl.png" }, Color.RED),
        /** powerup dodatkowe punkty */
        POWERUP_POINTS(new String[] { "sprites/POWERUP_+200.png" }, Color.RED),
        /** powerup dodatkowa prędkość */
        POWERUP_SPEED(new String[] { "sprites/POWERUP_SKRZYDLO.png" }, Color.RED),
        /** powerup nioezaimplementowany */
        POWERUP_ICE(new String[] { "sprites/POWERUP_LOD.png" }, Color.RED),
        /** placeholder domyslnego powerupa */
        POWERUP(new String[] { "sprites/powerup.png" }, Color.RED);

        /** scieżki do plików */
        private final String[] spritePaths;
        /** kolor placeholdera */
        private final Color placeholderColor;
        /** tablica spriteów */
        private Image[] sprites;
        /** tablica spriteów przeskalowanych */
        private Image[] scaledSprites;

        /**
         * @brief Konstruktor typu sprite'a
         * @param paths            Tablica ścieżek do plików graficznych
         * @param placeholderColor Kolor zastępczy
         */
        Type(String[] paths, Color placeholderColor) {
            this.spritePaths = paths;
            this.placeholderColor = placeholderColor;
            this.sprites = loadSprites(paths, placeholderColor);
            this.scaledSprites = this.sprites;
        }

        /**
         * @brief Ładuje sprite'y z podanych ścieżek
         * @param paths            Tablica ścieżek do plików
         * @param placeholderColor Kolor zastępczy dla nieudanych załadowań
         * @return Tablica załadowanych obrazów
         */
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

        /**
         * @brief Ładuje pojedynczy sprite z pliku
         * @param path             Ścieżka do pliku graficznego
         * @param placeholderColor Kolor zastępczy w przypadku błędu
         * @return Załadowany obraz lub placeholder
         */
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

        /**
         * @brief Tworzy placeholder w przypadku braku sprite'a
         * @param color Kolor placeholdera zastępczego
         * @return Obraz placeholder'a
         */
        private static Image createPlaceholder(Color color) {
            int size = 64;
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    img.setRGB(y, size - 1 - x, color.getRGB());
                }
            }


            Graphics2D g2 = img.createGraphics();
            g2.setColor(color);
            g2.fillRect(0, 0, size, size);
            g2.setColor(Color.BLACK);
            g2.drawRect(0, 0, size - 1, size - 1);
            g2.dispose();
            return img;
        }

        /**
         * @brief Pobiera sprite na podstawie postępu animacji
         * @param animProgress Postęp animacji (0.0 - 1.0)
         * @param direction    Kierunek (1-4) dla rotacji sprite'a
         * @return Sprite odpowiadający postępowi animacji z rotacją
         * @details Mapuje postęp animacji na indeksy klatek:
         *          - 0.0-0.3: klatka 0
         *          - 0.3-0.6: klatka 1
         *          - 0.6-1.0: klatka 2
         */
        public Image getSprite(double animProgress, int direction) {
            if (animProgress < 0.25)
                return getSprite(0, direction);
            else if (animProgress < 0.50)
                return getSprite(1, direction);
            else if (animProgress < 0.75)
                return getSprite(2, direction);
            else if (animProgress < 1)
                return getSprite(1, direction);
            else
                return getSprite(0, direction);

        }

        /**
         * @brief Pobiera sprite z rotacją
         * @param idx       Indeks sprite'a w tablicy
         * @param direction Kierunek rotacji (1=90°, 2=180°, 3=270°, 4=0°)
         * @return Obrócony sprite
         * @details Tworzy nowy obraz z rotacją zgodnie z kierunkiem:
         *          - 1: obrót o 90° (w prawo)
         *          - 2: obrót o 180° (w dół)
         *          - 3: obrót o 270° (w lewo)
         *          - 4: bez rotacji (w górę)
         */
        public Image getSprite(int idx, int direction) {
            Image img;

            if (idx < 0 || idx >= scaledSprites.length) {
                img = scaledSprites[0];
            } else
                img = scaledSprites[idx];

            // BufferedImage buffImage = new BufferedImage(
            //         img.getWidth(null),
            //         img.getHeight(null),
            //         BufferedImage.TYPE_INT_ARGB);
                    //rotate(buffImage,direction);
            // Graphics2D g = buffImage.createGraphics();
            
            // g.translate(img.getWidth(null) / 2, img.getHeight(null) / 2);

            // switch (direction) {
            //     case 1 -> g.rotate(Math.toRadians(90));
            //     case 2 -> g.rotate(Math.toRadians(180));
            //     case 3 -> g.rotate(Math.toRadians(270));
            //     case 4 -> g.rotate(Math.toRadians(0));
            //     default -> {
            //     }
            // }

            // g.drawImage(img, -img.getWidth(null) / 2, -img.getHeight(null) / 2, null);
            // g.dispose();
            return (Image) rotate((BufferedImage) img,direction);
        }

        public BufferedImage rotate(BufferedImage src, int kierunek) {
            int w = src.getWidth();
            int h = src.getHeight();
           

            if (kierunek != 1 && kierunek != 2 && kierunek != 3) {
                BufferedImage copy = new BufferedImage(w, h, src.getType());
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        copy.setRGB(x, y, src.getRGB(x, y));
                    }
                }
                return copy;
            }
    
            BufferedImage dst;
            switch (kierunek) {
                case 1: 
                    dst = new BufferedImage(h, w, src.getType());
                    for (int x = 0; x < w; x++) {
                        for (int y = 0; y < h; y++) {
                            dst.setRGB(h - 1 - y, x, src.getRGB(x, y));
                        }
                    }
                    return dst;
    
                case 2: 
                    dst = new BufferedImage(w, h, src.getType());
                    for (int x = 0; x < w; x++) {
                        for (int y = 0; y < h; y++) {
                            dst.setRGB(w - 1 - x, h - 1 - y, src.getRGB(x, y));
                        }
                    }
                    return dst;
    
                case 3: 
                    dst = new BufferedImage(h, w, src.getType());
                    for (int x = 0; x < w; x++) {
                        for (int y = 0; y < h; y++) {
                            dst.setRGB(y, w - 1 - x, src.getRGB(x, y));
                        }
                    }
                    return dst;
    
                default:
                    // teoretycznie nigdy tu nie wpadnie
                    return src;
            }
        }

        /**
         * @brief Pobiera sprite o podanym indeksie
         * @param idx Indeks sprite'a w tablicy
         * @return Sprite bez rotacji
         */
        public Image getSprite(int idx) {
            if (idx < 0 || idx >= sprites.length) {
                return scaledSprites[0];
            }
            return scaledSprites[idx];
        }

        /**
         * @brief Pobiera tablicę wszystkich sprite'ów
         * @return Tablica oryginalnych sprite'ów
         */
        public Image[] getSprites() {
            return sprites;
        }

        /**
         * @brief Przeskalowuje wszystkie sprite'y do nowego rozmiaru
         * @param w Szerokość
         * @param h Wysokość
         * @details Tworzy nową tablicę przeskalowanych sprite'ów używając
         *          interpolacji bilinearnej dla lepszej jakości
         */
        public void rescale(int w, int h) {

            Image[] tmp = new Image[sprites.length];
            for (int i = 0; i < sprites.length; i++)
                tmp[i] = scale(sprites[i], w, h);

            this.scaledSprites = tmp;
        }

        /**
         * @brief Skaluje pojedynczy obraz
         * @param src Obraz źródłowy
         * @param w   Docelowa szerokość
         * @param h   Docelowa wysokość
         * @return Przeskalowany obraz
         * @details Używa interpolacji bilinearnej dla lepszej jakości skalowania
         */
        // private static Image scale(Image src, int w, int h) {
        //     BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        //     Graphics2D g = dst.createGraphics();
        //     g.drawImage(src, 0, 0, w, h, null);
        //     g.dispose();
        //     return dst;
        // }


            /**
     * Skalowanie najbliższego sąsiada (nearest‐neighbor) bez Graphics2D.
     *
     * @param src Wejściowy Image
     * @param newW Docelowa szerokość
     * @param newH Docelowa wysokość
     * @return Nowy Image o rozmiarze newW×newH
     */
    public static Image scale(Image src, int newW, int newH) {
        BufferedImage tmp = (BufferedImage) src;
        int srcW = tmp.getWidth();
        int srcH = tmp.getHeight();
        BufferedImage dst = new BufferedImage(newW, newH, tmp.getType());

        
        double xRatio = (double) srcW  / newW;
        double yRatio = (double) srcH  / newH;

        for (int y = 0; y < newH; y++) {
            int srcY = (int) (y * yRatio);
            if (srcY >= srcH) srcY = srcH - 1;

            for (int x = 0; x < newW; x++) {
                int srcX = (int) (x * xRatio);
                if (srcX >= srcW) srcX = srcW - 1;
                int rgb = tmp.getRGB(srcX, srcY);
                dst.setRGB(x, y, rgb);
            }
        }
        return (Image)dst;
    }

    }

    /** Typ sprite'a */
    public Type type;
    /** Dodatkowa wartość liczbowa powiązana z komórką */
    public Agent agent;

    /**
     * @brief Konstruktor tworzący SpriteCellType z domyślną wartością 0
     * @param type Typ sprite'a
     */
    public SpriteCellType(Type type) {
        this(type, null);
    }

    /**
     * @brief Konstruktor tworzący SpriteCellType z określoną wartością
     * @param type Typ sprite'a
     */
    public SpriteCellType(Type type, Agent agent) {
        this.type = type;
        this.agent = agent;
    }
    
}