package controller;

import java.io.*;
import java.util.ArrayList;

/**
 * @class SerializableList
 * @brief Klasa obsługująca listę wyników
 *
 * Klasa rozszerza dostępną ArrayList o możliwość zapisu i ładowania z pliku
 */


public class SerializableList<T extends Serializable>
        extends ArrayList<T> {

    public SerializableList() {
        super();
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }

    public static <T extends Serializable>
    SerializableList<T> loadFromFile(String filename)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(filename))) {
            return (SerializableList<T>) ois.readObject();
        }
    }
}