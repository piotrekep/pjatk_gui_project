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

        int x = position.x;                 // wiersz   (rosnący w dół)
        int y = position.y;                 // kolumna (rosnący w prawo)
    
        int rows = distField.length;        // liczba wierszy
        int cols = distField[0].length;     // liczba kolumn
    
        int bestDir  = 0;                   // 0 = brak legalnego ruchu
        int bestDist = Integer.MAX_VALUE;   // szukamy MIN
    
        /* ----------- 1) góra (x-1,y) ---------------- */
        if (x > 0) {
            int v = distField[x - 1][y];
            if (v >= 0 && v < bestDist) {   // legalne pole i lepszy dystans
                bestDist = v;
                bestDir  = 1;               // ↑
            }
        }
        /* ----------- 2) prawo (x,y+1) -------------- */
        if (y + 1 < cols) {
            int v = distField[x][y + 1];
            if (v >= 0 && v < bestDist) {
                bestDist = v;
                bestDir  = 2;               // →
            }
        }
        /* ----------- 3) dół (x+1,y) ---------------- */
        if (x + 1 < rows) {
            int v = distField[x + 1][y];
            if (v >= 0 && v < bestDist) {
                bestDist = v;
                bestDir  = 3;               // ↓
            }
        }
        /* ----------- 4) lewo (x,y-1) --------------- */
        if (y > 0) {
            int v = distField[x][y - 1];
            if (v >= 0 && v < bestDist) {
                bestDist = v;
                bestDir  = 4;               // ←
            }
        }
    
        /* ————  przekazujemy decyzję Twojej logice sterowania ———— */
        if (bestDir != 0) {
            setDirection(bestDir);          // ustawia newDirection
        }
        changeDirection();                  // sprawdza ściany i podmienia direction
        move(speed);                        // faktyczne przesunięcie sprite’a
    }


}




