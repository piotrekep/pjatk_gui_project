package model;

/**
 * @class Powerup
 * @brief byt typu powerup
 * klasa definiuje byty typu powerup
 */
public class Powerup extends Agent {

    private PowerupType powerup;

    public Powerup(int x, int y, int id, CellType[][] level, PowerupType powerup){
        super(x, y, id, level);
        this.powerup=powerup;
    }

    public PowerupType getPowerup() {
        return powerup;
    }

    public void checkCollision(int[][] distField){
        if(distField[position.x][position.y] == 0 )
            if (listener != null)
                listener.onCollision(this);
    }

}
