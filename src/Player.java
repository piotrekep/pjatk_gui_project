public class Player extends Agent {

    private int direction=0;
    private int newDirection=0;

    public Player(int x, int y, String name, CellType[][] level) {
        super(x, y, name, level);
    }

    
    @Override
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
    
    @Override
    public void setDirection(int direction){
        switch (direction) {
            case 1 -> this.newDirection = 1;
            case 2 -> this.newDirection = 2;
            case 3 -> this.newDirection = 3;
            case 4 -> this.newDirection = 4;
            default ->{}
        }
    }

    @Override
    public void moveUp(){
        if(level[position.x-1][position.y] != CellType.WALL &&
         level[position.x-1][position.y] != CellType.GHOSTHOUSE &&
          level[position.x-1][position.y] != CellType.GHOSTFLOOR)

            position.x--;
    }
    
    @Override
    public void moveDown(){
        if(level[position.x+1][position.y] != CellType.WALL &&
        level[position.x+1][position.y] != CellType.GHOSTHOUSE &&
        level[position.x+1][position.y] != CellType.GHOSTFLOOR)
        
            position.x++;
    }
    
    @Override
   public void moveLeft(){
    if(level[position.x][position.y-1] != CellType.WALL &&
    level[position.x][position.y-1] != CellType.GHOSTHOUSE &&
    level[position.x][position.y-1] != CellType.GHOSTFLOOR)

            position.y--;
    }
    
    @Override
    public void moveRight(){
        if(level[position.x][position.y+1] != CellType.WALL &&
        level[position.x][position.y+1] != CellType.GHOSTHOUSE &&
        level[position.x][position.y+1] != CellType.GHOSTFLOOR)

            position.y++;
}
}
