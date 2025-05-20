package model;


public class Player extends Agent {
    private long lastTime;
    private int points = 0;
    private int bonusPoints = 0;
    private int lives = 0;
    public boolean powered=false;
    public long pearlTime=0;
    public long speedTime=0;
    private  PowerupType collected; 

    public Player(int x, int y, int id, CellType[][] level) {
        super(x, y, id, level);
        lastTime = System.nanoTime();
    }

    // prekość w tickach na sekunde
    public boolean move(double speed) {

        if(super.move(speed)){
            checkForPoint();
            return true;
        }
        else
            return false;
        }


    @Override
    public void move() {
        super.move();
        checkForPoint();
    }

    private void checkForPoint() {
        if (level[position.x][position.y] == CellType.POINT) {
            points++;
            level[position.x][position.y] = CellType.EMPTY;
        }

    }
    public void stopPlayer(){
        newDirection=0;
        direction=0;
    }

    public int getPoints() {
        return points;
    }

    public int getTotalPoints() {
        return points+bonusPoints;
    }

    public void addBonusPoints(int points){
        this.bonusPoints+=points;
    }

    public void setLives(int lives){  
      this.lives=lives;
    }

    public int getLives(){
        return this.lives;
    }

    public PowerupType getPowerup(){
        return collected;
    }

    public void setPowerup(PowerupType pwr){
        this.collected=pwr;
    }

    @Override
    public void setDirection(int direction) {
        switch (direction) {
            case 1 -> this.newDirection = 1;
            case 2 -> this.newDirection = 2;
            case 3 -> this.newDirection = 3;
            case 4 -> this.newDirection = 4;
            default -> {this.newDirection = 0;}
        }
    }

    @Override
    public void moveUp() {
        if (level[position.x - 1][position.y] != CellType.WALL &&
                level[position.x - 1][position.y] != CellType.GHOSTHOUSE &&
                level[position.x - 1][position.y] != CellType.GHOSTFLOOR)

            position.x--;
    }

    @Override
    public void moveDown() {
        if (level[position.x + 1][position.y] != CellType.WALL &&
                level[position.x + 1][position.y] != CellType.GHOSTHOUSE &&
                level[position.x + 1][position.y] != CellType.GHOSTFLOOR)

            position.x++;
    }

    @Override
    public void moveLeft() {
        if (level[position.x][position.y - 1] != CellType.WALL &&
                level[position.x][position.y - 1] != CellType.GHOSTHOUSE &&
                level[position.x][position.y - 1] != CellType.GHOSTFLOOR)

            position.y--;
    }

    @Override
    public void moveRight() {
        if (level[position.x][position.y + 1] != CellType.WALL &&
                level[position.x][position.y + 1] != CellType.GHOSTHOUSE &&
                level[position.x][position.y + 1] != CellType.GHOSTFLOOR)

            position.y++;
    }

 
}
