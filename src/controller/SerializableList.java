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
        try (ObjectOutputStream stream =
                     new ObjectOutputStream(new FileOutputStream(filename))) {
            stream.writeObject(this);
        }
    }

    public static <T extends Serializable>
    SerializableList<T> loadFromFile(String filename) {
                try {
                    ObjectInputStream stream = new ObjectInputStream(new FileInputStream(filename));
                    return (SerializableList<T>) stream.readObject();
                } catch (Exception e) {
                    return new SerializableList<T>();
                }
    }
}