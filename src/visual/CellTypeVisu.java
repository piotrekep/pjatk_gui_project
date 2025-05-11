package visual;

import java.awt.Color;

public enum CellTypeVisu {

    EMPTY (Color.WHITE ),   
    WALL  (Color.DARK_GRAY),
    PLAYER(Color.BLUE), 
    NPC1  (Color.GREEN ), 
    NPC2   (Color.ORANGE ), 
    GHOSTHOUSE   (Color.GRAY), 
    GHOSTFLOOR   (Color.LIGHT_GRAY); 

    private final Color color;   

    CellTypeVisu(Color color) {
        this.color  = color;
    }

    public Color getColor()  { return color; }

}