package model;

import java.util.Random;

public class Npc extends Agent {
    Random rnd;
    int rndDir;

    public Npc(int x, int y, String name, CellType[][] level) {
        super(x, y, name, level);
        rnd = new Random();
        rndDir = rnd.nextInt(1, 4);
    }

    public void moveRandom(double speed){
      
        //setDirection(rndDir);

        if(!moveInDirPossible(direction)){
            rndDir = (int) Math.round(rnd.nextDouble(1, 4));
            setDirection(rndDir);
            changeDirection();
        }
 
        move(speed);
        System.out.println(rndDir);
    }
      
}




