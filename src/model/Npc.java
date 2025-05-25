package model;

import java.util.Random;

/**
 * @class Npc
 * @brief Definicja npc w grze
 *
 *        Klasa rozszerza klasę Agent o dodatkową funkcjonalność odróżniającą
 *        byt npc od innych.
 */

public class Npc extends Agent {
/** obiekt generatora liczb losowych */
    Random rnd;
/** losowy kierunek poruszania się  */
    int rndDir;
    /**"osobowość przeciwnika". Algorytm poruszania się przeciwnika jest zależny od jego osobowości */
    private Personality personality = Personality.AGGRO;
    /** początkowa osobowość */
    private Personality orignalPersonality;
    /**
     * konstruktor npc.
     *
     * @param x     współrzędna wiersza startowego
     * @param y     współrzędna kolumny startowej
     * @param id    unikalny identyfikator agenta
     * @param level mapa gry
     * @param p     osobowość
     */

    public Npc(int x, int y, int id, CellType[][] level, Personality p) {
        super(x, y, id, level);
        rnd = new Random();
        rndDir = rnd.nextInt(1, 4);
        this.orignalPersonality=p;
        this.personality=p;
    }

/** 
 * Algorytm poruszania się w losowym kierunku
 * @param speed prędkość ruchu w kratka na sekunde
 *  */

    public void moveRandom(double speed) {

        if (!moveInDirPossible(direction)) {
            rndDir = (int) Math.round(rnd.nextDouble(1, 4));
            setDirection(rndDir);
        }

        move(speed);
    }

    /**
     * implementacja A-Star dla przeciwników
     * @param speed     prędkość poruszania się
     * @param distField pole wektorów odległości od celu.
     */
    public void moveAstar(double speed, int[][] distField) {
        //inicjacja wyboru kierunku i odległości
        int bestDir = 0;
        int bestDist = Integer.MAX_VALUE;

        //sprawdza czy ruch kierunku = 1  jest możliwy
        if (position.x > 0) {
            //pobiera odległość do celu z pola odległości
            int v = distField[position.x - 1][position.y];
            //sprawdza czy odległość większa niż zero (odległość zero znaczy, że jesteśmy u celu)
            if (v >= 0)
            //sprawdza czy aktualna odległość jest najlepsza
                if (v < bestDist) {
                    //jeżeli najlepsza to ustaw ją jako nową najlepszą
                    bestDist = v;
                    bestDir = 1;
                }
                //jeżeli odległość w kierunku 1 jest równa innej, ale nie jest od niej lepsza to 50% szans na zmiane kierunku
                else if( v == bestDist && Math.random()>0.5)
                    bestDir = 1;
        }

        if (position.y + 1 < distField[0].length) {
            int v = distField[position.x][position.y + 1];
            if (v >= 0)
                if (v < bestDist) {
                    bestDist = v;
                    bestDir = 2;
                }
                else if( v == bestDist && Math.random()>0.5)
                    bestDir = 2;
        }

        if (position.x + 1 < distField.length) {
            int v = distField[position.x + 1][position.y];
            if (v >= 0)
                if (v < bestDist) {
                    bestDist = v;
                    bestDir = 3;
                }
                else if( v == bestDist && Math.random()>0.5)
                    bestDir = 3;
        }

        if (position.y > 0) {
            int v = distField[position.x][position.y - 1];
            if (v >= 0)
                if (v < bestDist) {
                    bestDist = v;
                    bestDir = 4;
                }
                else if( v == bestDist && Math.random()>0.5)
                    bestDir = 4;
        }
        //jeżeli mamy nowy najlepszy kierunek to ustaiwamy go jako kierunek dla npc
        if (bestDir != 0) {
            setDirection(bestDir);
        }
        //wykonujemy ruch
        move(speed);
    }


    /**
     * implementacja odwróconego A-Star dla przeciwników
     * zamiast dążyć do minimum, dąży do maksimum uciekając od gracza
     * @param speed     prędkość poruszania się
     * @param distField pole wektorów odległości od celu.
     */

    public void moveAstarInv(double speed, int[][] distField) {

        int bestDir  = 0;
        int bestDist = -1;  
    
       
        if (position.x > 0) {
            int v = distField[position.x - 1][position.y];
            if (v >= 0) {
                if (v > bestDist) {
                    bestDist = v;
                    bestDir  = 1;
                } else if (v == bestDist && Math.random() > 0.5) {
                    bestDir  = 1;
                }
            }
        }
    
       
        if (position.y + 1 < distField[0].length) {
            int v = distField[position.x][position.y + 1];
            if (v >= 0) {
                if (v > bestDist) {
                    bestDist = v;
                    bestDir  = 2;
                } else if (v == bestDist && Math.random() > 0.5) {
                    bestDir  = 2;
                }
            }
        }
    
        
        if (position.x + 1 < distField.length) {
            int v = distField[position.x + 1][position.y];
            if (v >= 0) {
                if (v > bestDist) {
                    bestDist = v;
                    bestDir  = 3;
                } else if (v == bestDist && Math.random() > 0.5) {
                    bestDir  = 3;
                }
            }
        }
    
       
        if (position.y > 0) {
            int v = distField[position.x][position.y - 1];
            if (v >= 0) {
                if (v > bestDist) {
                    bestDist = v;
                    bestDir  = 4;
                } else if (v == bestDist && Math.random() > 0.5) {
                    bestDir  = 4;
                }
            }
        }
    
        if (bestDir != 0) {
            setDirection(bestDir);
        }
        
        move(speed);
    }

/**
 * Wykonuje ruch przy pomocy algorytmu zależnego od osobowości
 * 
 * @param speed     prędkośc w kratkach na sekunde
 * @param thresh    odległość dla niektórych osobowości
 * @param distField pole wektorów odległości od celu.
 */
    void movePersonality(double speed, int thresh, int[][] distField) {
        Personality p;
        if(level[position.x][position.y]==CellType.GHOSTFLOOR)
            p=Personality.CHASER;
        else
            p=personality;

        switch (p) {
            case Personality.CHASER -> moveChaser(speed, distField);
            case Personality.HEADLESSCHICKEN -> moveChicken(speed);
            case Personality.KEYBOARDWARRIOR -> moveKeyboardWarrior(speed, thresh, distField);
            case Personality.AGGRO -> moveAggro(speed, thresh, distField);
            case Personality.COWARD -> moveCoward(speed, distField);
            case Personality.POWERUP -> moveCoward(speed, distField);
            default -> {
            }
        }

        if(distField[position.x][position.y] == 0 )
            if (listener != null)
              listener.onCollision(this);

    }

 
/**
 * Ruch dla osobowości "chaser" npc będzie zawsze dążył do celu
 * @param speed     prędkośc w kratkach na sekunde
 * @param distField pole odległości
 */
    void moveChaser(double speed, int[][] distField) {
        moveAstar(speed, distField);

    }

/**
 * Ruch dla osobowości "tchórz" npc będzie zawsze uciekał jak najdalej od celu
 * @param speed     prędkośc w kratkach na sekunde
 * @param distField pole odległości
 */
    void moveCoward(double speed, int[][] distField) {
        moveAstarInv(speed, distField);

    }

/**
 * Ruch dla osobowości "headles chicken" npc będzie zawsze chodził losowo
 * @param speed     prędkośc w kratkach na sekunde
 * @param distField pole odległości
 */
    void moveChicken(double speed) {
        moveRandom(speed);
    }

/**
 * Ruch dla osobowości agresor npc będzie dążył do celu tylko jeśli cel podejdzie dostatecznie blisko
 * @param speed     prędkośc w kratkach na sekunde
 * @param distField pole odległości
 */

    void moveAggro(double speed, int thresh, int[][] distField) {
        if (distField[position.x][position.y] < thresh)
            moveAstar(speed, distField);
        else
            moveRandom(speed);
    }

/**
 * Ruch dla osobowości "keyboard warrior"  będzie gonił cel, do czasu aż odległość będzie mniejsza niż thrsh
 * jeżeli gracz będzie zbyt blisko, straci odwage i zacznie poruszać się losowo
 * @param speed     prędkośc w kratkach na sekunde
 * @param distField pole odległości
 */

    void moveKeyboardWarrior(double speed, int thresh, int[][] distField) {
        if (distField[position.x][position.y] > thresh)
            moveAstar(speed, distField);
        else
            moveRandom(speed);
    }
/**
 * ustawia osobowość
 * @param p osobowość
 */
    public void setPersonality(Personality p) {
        this.personality = p;
    }
/**
 * pobiera osobowość
 * @return osobowość
 */
    public Personality getPersonality() {
        return this.personality;
    }

    /**
     * reset osobowości do domyślnej ustawionej w konstruktorze
     */
    public void resetPersonality(){
        this.personality=this.orignalPersonality;
    }

    /**
     * zwraca rodzaj twła komórki planszy zależny od osobowości
     * @return typ komórki
     */
    public CellType getCellType() {
        switch (personality) {
            case Personality.CHASER: {
                return CellType.NPC_CHASER;
            }
            case Personality.AGGRO: {
                return CellType.NPC_AGGRO;
            }
            case Personality.KEYBOARDWARRIOR: {
                return CellType.NPC_KEYBOARDWARRIOR;
            }
            case Personality.HEADLESSCHICKEN: {
                return CellType.NPC_HEADLESSCHICKEN;
            }
            case Personality.COWARD: {
                return CellType.NPC_COWARD;
            }
            default: {
                return CellType.NPC_CHASER;
            }
        }
    }

}
