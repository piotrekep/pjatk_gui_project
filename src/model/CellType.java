package model;

import java.awt.Color;

public enum CellType {

    EMPTY (Color.WHITE , '.'),   
    WALL  (Color.DARK_GRAY, '#'),
    PLAYER(Color.BLUE     , 'P'), 
    NPC1  (Color.GREEN   , 'G'), 
    NPC2   (Color.ORANGE  , 'B'), 
    GHOSTHOUSE   (Color.GRAY  , 'B'), 
    GHOSTFLOOR   (Color.LIGHT_GRAY  , '-'); 

    private final Color color;   
    private final char  symbol;  

    CellType(Color color, char symbol) {
        this.color  = color;
        this.symbol = symbol;
    }

    public Color getColor()  { return color; }
    public char  getSymbol() { return symbol; }

    /* Zamiana znaku wczytanego z pliku na CellType */
    public static CellType fromSymbol(char ch) {
        for (CellType ct : values()) {
            if (ct.symbol == ch) return ct;
        }
        throw new IllegalArgumentException("Nieznany symbol pola: " + ch);
    }
}