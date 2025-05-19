package model;

import java.awt.Point;

abstract public class Agent {    
    public interface AgentListener {
    void onCollision(Agent source);
    void onChangePosition(Agent source);
}
    protected AgentListener listener;


    public Point position;
    public boolean spawned = false;
    public int id;
    protected CellType[][] level;
    private long lastTime;
    protected int direction=0;
    protected int newDirection=0;
    private Point spawPoint;
    private Point target;
    private double moveProgress = 1.0;
    private double speed=0.0;


    Agent(int x, int y, int id, CellType[][] level) {
        this.position = new Point(x, y);
        this.spawPoint = new Point(x, y);
        this.target = new Point(x, y);
        this.lastTime = System.nanoTime();

        this.id = id;
        this.level = level;
    }
    public void updateLevel(CellType[][] level){
        this.level=level;
    }


    public void setListener(AgentListener l) {
        this.listener = l;
    }

     


    public boolean move(double speed){
        this.speed=speed;
        long now = System.nanoTime();
        moveProgress = (now - lastTime)/1_000_000_000.0;

        changeDirection();
        if(speed!=0){
            switch (direction) {
                case 1:{
                    target.x = position.x-1;
                    target.y = position.y;
                    break;
                } 
                case 2:{
                    target.y = position.y+1;
                    target.x = position.x;
                    break;
                }
                case 3:{
                    target.x = position.x+1;
                    target.y = position.y;
                    break;
                }
                case 4:{
                    target.y = position.y-1;
                    target.x = position.x;
                    break;
                }
                default:{break;}
         }
         if(level[target.x][target.y]==CellType.WALL || level[target.x][target.y]==CellType.GHOSTHOUSE  ){
            target.x=position.x;
            target.y=position.y;
         }

        if(moveProgress > 1.0/speed){
            lastTime = now;
            switch (direction) {
                case 1 -> moveUp();
                case 2 -> moveRight();
                case 3 -> moveDown();
                case 4 -> moveLeft();
                default ->{}
         }
         if(listener!=null)
            listener.onChangePosition(this);
         return true;
        }
    }
        return false;
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
            default ->{this.newDirection = 0;}
        }
    }

    public void setPosition(int x, int y){
        this.position.x=x;
        this.position.y=y;
        this.target.x=x;
        this.target.y=y;
           
    }

    public void moveToSpawn(){
        this.position.x=this.spawPoint.x;
        this.position.y=this.spawPoint.y;
        this.target.x=this.spawPoint.x;
        this.target.y=this.spawPoint.y;
        direction=0;
        newDirection=0;
    }

    protected void changeDirection(){
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

    public int getCol(){
        return position.y;
    }


    public int getRow(){
        return position.x;
    }
    public int getTargetCol(){
        return target.y;
    }

    public int getTargetRow(){
        return target.x;
    }

    public double getMoveProgress(){
        return Math.min(1.0, Math.max(0.0, moveProgress/(1.0/this.speed)));
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

    public boolean moveInDirPossible(int direction){
        switch (direction) {
            case 1: return moveUpPossible();
            case 2: return moveRightPossible();
            case 3: return moveDownPossible();
            case 4: return moveLeftPossible();
            default: return false;
        }
    }
}
