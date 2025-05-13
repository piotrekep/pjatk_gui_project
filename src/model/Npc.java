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
    }




    public void moveAstar(double speed, int[][] distField) { 
    
        int bestDir  = 0;                  
        int bestDist = Integer.MAX_VALUE;   
    
        
        if (position.x > 0) {
            int v = distField[position.x - 1][position.y];
            if (v >= 0 && v < bestDist) {   
                bestDist = v;
                bestDir  = 1;              
            }
        }
      
        if (position.y + 1 < distField[0].length) {
            int v = distField[position.x][position.y + 1];
            if (v >= 0 && v < bestDist) {
                bestDist = v;
                bestDir  = 2;               
            }
        }
       
        if (position.x + 1 < distField.length) {
            int v = distField[position.x + 1][position.y];
            if (v >= 0 && v < bestDist) {
                bestDist = v;
                bestDir  = 3;             
            }
        }
   
        if (y > 0) {
            int v = distField[position.x][position.y - 1];
            if (v >= 0 && v < bestDist) {
                bestDist = v;
                bestDir  = 4;            
            }
        }
    
        if (bestDir != 0) {
            setDirection(bestDir);        
        }
        changeDirection();                
        move(speed);                        
    }


}




