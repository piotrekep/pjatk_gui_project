package visual;

import java.awt.Color;

/**
 * @class CellTypeVisu
 * @brief Klasa wizualizacji używana przed wprowadzeniem spriteów
 * 
 */

public class CellTypeVisu {

    public enum Type {
        EMPTY(Color.WHITE),
        WALL(Color.DARK_GRAY),
        PLAYER(Color.BLUE),
        NPC_CHASER(Color.GREEN),
        NPC_AGGRO(Color.YELLOW),
        NPC_KEYBOARDWARRIOR(Color.MAGENTA),
        NPC_HEADLESSCHICKEN(Color.CYAN),
        NPC_COWARD(Color.ORANGE),
        NPC_POWERUP(Color.ORANGE),
        GHOSTHOUSE(Color.GRAY),
        GHOSTFLOOR(Color.PINK),
        POINT(Color.LIGHT_GRAY),
        POWERUP_LIFE(Color.RED),
        POWERUP_PEARL(Color.RED),
        POWERUP_POINTS(Color.RED),
        POWERUP_SPEED(Color.RED),
        POWERUP_ICE(Color.RED),
        POWERUP(Color.RED);

        public final Color color;

        Type(Color color) {
            this.color = color;
        }
    }

    public Type type;
    public int val;

    public Color getColor() {
        return type.color;
    }

    public CellTypeVisu(Type type) {
        this.type = type;
        this.val = 0;
    }
}
