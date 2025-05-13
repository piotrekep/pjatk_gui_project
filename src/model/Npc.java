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
   
        if (position.y > 0) {
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


    public void moveAstarInv(double speed, int[][] distField)  {

        int bestDir  = 0;          // 0 = no valid move found yet
        int bestDist = -1;         // start lower than any legal cell value (-1 = unreachable)
    
        // LEFT  (x-1, y)
        if (position.x > 0) {
            int v = distField[position.x - 1][position.y];
            if (v >= 0 && v > bestDist) {      // prefer the *largest* reachable value
                bestDist = v;
                bestDir  = 1;
            }
        }
    
        // DOWN  (x, y+1)      — Y grows downward in most 2-D arrays
        if (position.y + 1 < distField[0].length) {
            int v = distField[position.x][position.y + 1];
            if (v >= 0 && v > bestDist) {
                bestDist = v;
                bestDir  = 2;
            }
        }
    
        // RIGHT (x+1, y)
        if (position.x + 1 < distField.length) {
            int v = distField[position.x + 1][position.y];
            if (v >= 0 && v > bestDist) {
                bestDist = v;
                bestDir  = 3;
            }
        }
    
        // UP    (x, y-1)
        if (position.y > 0) {
            int v = distField[position.x][position.y - 1];
            if (v >= 0 && v > bestDist) {
                bestDist = v;
                bestDir  = 4;
            }
        }
    
        // Commit to the best direction found (if any), then move.
        if (bestDir != 0) {
            setDirection(bestDir);
        }
        changeDirection();
        move(speed);
    }

    void movePersonality(double speed, int thresh,int[][] distField, Personality p){
        
        switch (p) {
            case Personality.CHASER -> moveChaser(speed, distField);
            case Personality.HEADLESSCHICKEN -> moveChicken(speed);
            case Personality.KEYBOARDWARRIOR -> moveKeyboardWarrior(speed, thresh, distField);
            case Personality.AGGRO -> moveAggro(speed, thresh, distField);
            case Personality.COWARD -> moveCoward(speed, distField);
            default -> {
            }
        }

    }

    void moveChaser(double speed, int[][] distField){
        moveAstar(speed, distField);

    }

    void moveCoward(double speed, int[][] distField){
        moveAstarInv(speed, distField);

    }

    void moveChicken(double speed){
        moveRandom(speed);
    }

    void moveAggro(double speed, int thresh,int[][] distField){
        if(distField[position.x][position.y] < thresh)
            moveAstar(speed, distField); 
        else
            moveRandom(speed);
    }

    void moveKeyboardWarrior(double speed, int thresh,int[][] distField){
        if(distField[position.x][position.y] > thresh)
            moveAstar(speed, distField); 
        else
            moveRandom(speed);
    }
}




