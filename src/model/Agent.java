package model;

import java.awt.Point;

abstract public class Agent {
    public Point position = new Point(0, 0);
    public boolean spawned = false;
    public String name;
    protected CellType[][] level;
    private long lastTime;
    private int direction=0;
    private int newDirection=0;

    Agent(int x, int y, String name, CellType[][] level) {
        this.position.x = x;
        this.position.y = y;
        this.name = name;
        this.level = level;
    }
    public void updateLevel(CellType[][] level){
        this.level=level;
    }

    public void move(double speed){

        long now = System.nanoTime();
        double elapsedSeconds = (now - lastTime)/1_000_000_000.0;

        changeDirection();
        if(speed!=0)
        if(elapsedSeconds > 1/speed){
            lastTime = now;
            switch (direction) {
                case 1 -> moveUp();
                case 2 -> moveRight();
                case 3 -> moveDown();
                case 4 -> moveLeft();
                default ->{}
         }
        }
    }

    public void move(){

        changeDirection();
            switch (direction) {
                case 1 -> moveUp();
                case 2 -> moveRight();
                case 3 -> moveDown();
                case 4 -> moveLeft();
                default ->{}
         }
    }

     public void setDirection(int direction){
        switch (direction) {
            case 1 -> this.newDirection = 1;
            case 2 -> this.newDirection = 2;
            case 3 -> this.newDirection = 3;
            case 4 -> this.newDirection = 4;
            default ->{}
        }
    }

    public void setPosition(int x, int y){
        this.position.x=x;
        this.position.y=y;
    }

    private void changeDirection(){
        switch (newDirection) {
            case 1 -> {
                if(moveUpPossible())
                    direction=newDirection;
            }
            case 2 ->{
                if(moveRightPossible())
                    direction=newDirection;
            }
            case 3 -> {
                if(moveDownPossible())
                    direction=newDirection;
            }
            case 4 -> {
                if(moveLeftPossible())
                    direction=newDirection;
            }
            default ->{}
        }
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
