package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @class Labirynth
 * @brief klasa generująca labirynt
 */

public class Labirynth {
    /** tablica przechowująca labirytn */
    public final CellType[][] labirynt;
    /** tablica odwiedzonych pól */
    private final boolean[][] visited;
    /** pole startowe */
    public final Point origin = new Point(3, 3);

    /**
     * konstruktor
     * 
     * @param X rozmiar X
     * @param Y rozmiar Y
     */
    Labirynth(int X, int Y) {
        this.labirynt = new CellType[X][Y];
        visited = new boolean[X][Y];

        for (int i = 0; i < labirynt.length; i++)
            for (int j = 0; j < labirynt[i].length; j++)
                labirynt[i][j] = CellType.WALL;
        makeFrame();
    }

    /**
     * metoda zamykająca plansze. tworzy obramowanie labiryntu
     */
    private void makeFrame() {
        for (int i = 0; i < visited.length; i++)
            for (int j = 0; j < visited[i].length; j++) {
                if (i == 0 || i == (visited.length - 1) || j == 0 || j == (visited[0].length - 1))
                    visited[i][j] = true;
            }

    }

    /**
     * rekurencyjna metoda kroku
     * 
     * @param x aktualna współrzędna X
     * @param y aktualna współrzędna Y
     */
    private void step(int x, int y) {
        visited[x][y] = true;
        labirynt[x][y] = CellType.EMPTY;
        List<Point> unvisited = getUnvisited(x, y);
        Random rnd = new Random();

        Collections.shuffle(unvisited, rnd);

        for (Point point : unvisited) {
            if (!visited[point.x][point.y]) {
                deleteWall(x, y, point.x, point.y);
                step(point.x, point.y);
            }
        }
    }

    /**
     * metoda tworząca labirynt
     */
    private void carve() {
        buildGhostHouse();

        step(3, 3);
        addRandomDoor();

        if (labirynt.length % 2 == 0)
            evenCleanupW();
        if (labirynt[0].length % 2 == 0)
            evenCleanupH();

    }

    /** metoda generująca labirynt */
    public void generate() {
        carve();
    }

    /**
     * metoda pobierająca nieodwiedzonych sąsiadów punktu
     * 
     * @param X współrzędna X punktu
     * @param Y współrzędna Y punktu
     * @return
     */
    private List<Point> getUnvisited(int X, int Y) {

        List<Point> punkty = new ArrayList<>();

        int[][] offsets = {
                { 2, 0 },
                { -2, 0 },
                { 0, 2 },
                { 0, -2 }
        };

        for (int[] off : offsets) {

            if ((X + off[0]) >= 0 && (X + off[0]) < visited.length
                    && (Y + off[1]) >= 0 && (Y + off[1]) < visited[(X + off[0])].length) {
                if (!visited[(X + off[0])][(Y + off[1])]) {
                    punkty.add(new Point((X + off[0]), (Y + off[1])));
                }
            }
        }

        return punkty;
    }

    /**
     * metoda usuwająca scianę miedzy punktami 1 i 2
     * 
     * @param X1 współrzędna X punktu 1
     * @param Y1 współrzędna Y punktu 1
     * @param X2 współrzędna X punktu 2
     * @param Y2 współrzędna Y punktu 2
     */
    private void deleteWall(int X1, int Y1, int X2, int Y2) {
            int midX = (X1 + X2) / 2;
            int midY = (Y1 + Y2) / 2;
            labirynt[midX][midY] = CellType.EMPTY;
            visited[midX][midY] = true;
    
    }

    /**
     * czyszczenie scian skrajnch parzystych rozmiarów. szansa 75%, że usunie blok,
     * ale tylko taki który nie jest poprzedzony pustym i nie sąsiaduje z
     * prostopadłą scianą
     */
    private void evenCleanupH() {
        for (int i = 1; i < labirynt.length - 1; i++)
            if (Math.random() < 0.75 && labirynt[i - 1][labirynt[0].length - 2] != CellType.EMPTY
                    && labirynt[i][labirynt[0].length - 3] != CellType.WALL)
                labirynt[i][labirynt[0].length - 2] = CellType.EMPTY;
    }

    /**
     * czyszczenie scian skrajnch parzystych rozmiarów. szansa 75%, że usunie blok,
     * ale tylko taki który nie jest poprzedzony pustym i nie sąsiaduje z
     * prostopadłą scianą
     * 
     */
    private void evenCleanupW() {
        for (int i = 1; i < labirynt[0].length - 1; i++)
            if (Math.random() < 0.75 && labirynt[labirynt.length - 2][i - 1] != CellType.EMPTY
                    && labirynt[labirynt.length - 3][i] != CellType.WALL)
                labirynt[labirynt.length - 2][i] = CellType.EMPTY;
    }

    /**
     * dodaje dodatkowe losowe przejścia
     */
    private void addRandomDoor() {
        for (int i = 2; i < labirynt.length - 2; i++)
            for (int j = 2; j < labirynt[i].length - 2; j++) {
                double rnd = Math.random();
                if ((labirynt[i][j] == CellType.WALL &&
                        (((labirynt[i + 1][j] == CellType.WALL || labirynt[i + 1][j] == CellType.GHOSTHOUSE)
                                && (labirynt[i - 1][j] == CellType.WALL || labirynt[i - 1][j] == CellType.GHOSTHOUSE)) ^
                                ((labirynt[i][j + 1] == CellType.WALL || labirynt[i][j + 1] == CellType.GHOSTHOUSE)
                                        && (labirynt[i][j - 1] == CellType.WALL
                                                || labirynt[i][j - 1] == CellType.GHOSTHOUSE))))
                        &&
                        rnd < 0.38)
                    labirynt[i][j] = CellType.EMPTY;
            }

    }

    /**
     * buduje dom duchów
     */
    private Point buildGhostHouse() {

        int sizeX = Math.max(3, Math.min(8, labirynt[0].length / 5));
        int sizeY = Math.max(3, Math.min(5, labirynt.length / 5));

        int originX = (labirynt[0].length - sizeX) / 2;
        int originY = (labirynt.length - sizeY) / 2;
        Point originPoint = new Point();
        ;

        originPoint.x = originX;
        originPoint.y = originY;

        for (int i = originY; i < originY + sizeY; i++) {
            for (int j = originX; j < originX + sizeX; j++) {
                if (!(i == originY && j > originX && j < originX + sizeX - 1)) {
                    labirynt[i][j] = CellType.GHOSTHOUSE;
                    visited[i][j] = true;
                } else {
                    labirynt[i][j] = CellType.GHOSTFLOOR;
                    visited[i][j] = true;
                }
            }
        }
        for (int i = originX; i < originX + sizeX; i++) {
            labirynt[originY][i] = CellType.EMPTY;
        }

        for (int i = originY + 1; i < originY + sizeY - 1; i++) {
            for (int j = originX + 1; j < originX + sizeX - 1; j++) {
                labirynt[i][j] = CellType.GHOSTFLOOR;
                visited[i][j] = true;
            }
        }
        return originPoint;
    }

}
