package visual;


import java.awt.Color;



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
        GHOSTHOUSE(Color.GRAY),
        GHOSTFLOOR(Color.PINK),
        POINT(Color.LIGHT_GRAY);

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

/*
public enum CellTypeVisu {
    

    EMPTY (Color.WHITE ),   
    WALL  (Color.DARK_GRAY),
    PLAYER(Color.BLUE), 
    NPC1  (Color.GREEN ), 
    NPC2   (Color.ORANGE ), 
    GHOSTHOUSE   (Color.GRAY), 
    GHOSTFLOOR   (Color.PINK),
    POINT (Color.LIGHT_GRAY);

    private final Color color;
    public int val=0;   

    CellTypeVisu(Color color) {
        this.color  = color;
    }

    public Color getColor()  { return color; }

}
    */