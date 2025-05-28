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

    public String getName() {
        return name;
    }
    public int getScore() {
        return score;
    }
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + score + "}";
    }
}