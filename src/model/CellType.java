package model;

/**
 * @enum CellType
 * @brief CellType definiuje typ pola na potrzeby logiki gry
 *
 *        Enum definiuje typ pola konieczny do obsługi logiki gry.
 *        Przechowuje wszystkie możliwe stany jakie może przyjąć pole planszy
 */

public enum CellType {

    EMPTY,
    WALL,
    PLAYER,
    NPC_CHASER,
    NPC_AGGRO,
    NPC_KEYBOARDWARRIOR,
    NPC_HEADLESSCHICKEN,
    NPC_COWARD,
    NPC_POWERUP,
    GHOSTHOUSE,
    GHOSTFLOOR,
    POINT,
    POWERUP_LIFE,
    POWERUP_PEARL,
    POWERUP_POINTS,
    POWERUP_SPEED,
    POWERUP_ICE,
    POWERUP

}