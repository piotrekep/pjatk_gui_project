package model;

import java.io.*;

public class PlayerScore implements Serializable { 

    private String name;
    private transient int score;  
    public PlayerScore(String name, int score) {
        this.name = name;
        this.score = score;
    }
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + score + "}";
    }
}