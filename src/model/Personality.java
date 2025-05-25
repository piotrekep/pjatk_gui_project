package model;

/**
 * @enum Personality
 * @brief Typy osobowości dla postaci NPC w grze.
 *
 *        Określa sposób zachowania npc na planszy. Każdy typ osobowości
 *        określa inne podejście do poruszania się.
 */

public enum Personality {
    /**
     * Podąża bezpośrednio za graczem (najkrótszą drogą).
     */
    CHASER,
    /**
     * Atakuje gracza gdy podejdzie za blisko.
     */
    AGGRO,
    /**
     * Podąża za graczem aż do osiągnięcia minimalnego dystancu.
     * następnie porusza się losowo
     */
    KEYBOARDWARRIOR,
    /**
     * Porusza się losowo
     */
    HEADLESSCHICKEN,
    /**
     * Unika gracza. próbuje utrzymać maksymalny dystans
     */
    COWARD,
    
    /**
     * Gracz pod wypoływem powerupa
     */
    POWERUP   
 

}