package model;

import java.awt.Point;

abstract public class Agent {
    public Point position = new Point(0, 0);
    public boolean spawned = false;
    public String name;
    protected CellType[][] level;

    Agent(int x, int y, String name, CellType[][] level) {
        this.position.x = x;
        this.position.y = y;
        this.name = name;
        this.level = level;
    }

    public void move() {
    }

    public void setDirection(int direction) {
    }

    public void moveUp() {
        if (level[position.x - 1][position.y] != CellType.WALL &&
                level[position.x - 1][position.y] != CellType.GHOSTHOUSE)
            position.x--;
    }

    public void moveDown() {
        if (level[position.x + 1][position.y] != CellType.WALL &&
                level[position.x + 1][position.y] != CellType.GHOSTHOUSE)
            position.x++;
    }

    public void moveLeft() {
        if (level[position.x][position.y - 1] != CellType.WALL &&
                level[position.x][position.y - 1] != CellType.GHOSTHOUSE)
            position.y--;
    }

    public void moveRight() {
        if (level[position.x][position.y + 1] != CellType.WALL &&
                level[position.x][position.y + 1] != CellType.GHOSTHOUSE)
            position.y++;
    }

    public boolean moveUpPossible() {
        if (level[position.x - 1][position.y] != CellType.WALL &&
                level[position.x - 1][position.y] != CellType.GHOSTHOUSE)
            return true;
        else
            return false;
    }

    public boolean moveDownPossible() {
        if (level[position.x + 1][position.y] != CellType.WALL &&
                level[position.x + 1][position.y] != CellType.GHOSTHOUSE)
            return true;
        else
            return false;
    }

    public boolean moveLeftPossible() {
        if (level[position.x][position.y - 1] != CellType.WALL &&
                level[position.x][position.y - 1] != CellType.GHOSTHOUSE)
            return true;
        else
            return false;
    }

    public boolean moveRightPossible() {
        if (level[position.x][position.y + 1] != CellType.WALL &&
                level[position.x][position.y + 1] != CellType.GHOSTHOUSE)
            return true;
        else
            return false;
    }
}
