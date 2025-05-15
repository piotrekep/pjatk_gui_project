package model;


public class Player extends Agent {
    private long lastTime;
    private int points = 0;
    private int lives = 0;



    public Player(int x, int y, int id, CellType[][] level) {
        super(x, y, id, level);
        lastTime = System.nanoTime();
    }

    // prekość w tickach na sekunde
    public void move(double speed) {

        long now = System.nanoTime();
        double elapsedSeconds = (now - lastTime) / 1_000_000_000.0;

        changeDirection();
        if (speed != 0) {
            if (elapsedSeconds > 1 / speed) {
                // System.out.println(elapsedSeconds);
                lastTime = now;
                switch (direction) {
                    case 1 -> moveUp();
                    case 2 -> moveRight();
                    case 3 -> moveDown();
                    case 4 -> moveLeft();
                    default -> {
                    }
                }
                checkForPoint();
            }
        }
    
    }

    @Override
    public void move() {

        changeDirection();
        switch (direction) {
            case 1 -> moveUp();
            case 2 -> moveRight();
            case 3 -> moveDown();
            case 4 -> moveLeft();
            default -> {
            }
        }
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

    public void setLives(int lives){  
      this.lives=lives;
    }

    public int getLives(){
        return this.lives;
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
