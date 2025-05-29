package visual;

import java.io.*;

/**
 * @class PlayerScore
 * @brief Klasa definiująca obiekt w tabeli punktacji
 */
public class PlayerScore implements Serializable {

    private String name;
    private int score;

    public PlayerScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * @brief getter nazwy gracz
     * @return nazwa gracz
     */
    public String getName() {
        return name;
    }

    /**
     * @brief getter punktów
     * @return punktacja
     */
    public int getScore() {
        return score;
    }

    /**
     * @brief overide toString
     */
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + score + "}";
    }
}