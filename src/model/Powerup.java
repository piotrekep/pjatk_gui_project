package model;

public class Powerup extends Agent {

    private PowerupType powerup;

    public Powerup(int x, int y, int id, CellType[][] level, PowerupType powerup){
        super(x, y, id, level);
        this.powerup=powerup;
    }

}
