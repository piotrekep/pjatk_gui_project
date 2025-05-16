package model;

import java.util.Random;


public class Npc extends Agent {
    public interface NpcListener {
        void onCollision();
    }

    private NpcListener listener;


    Random rnd;
    int rndDir;
    private Personality personality = Personality.AGGRO;
    private Personality orignalPersonality;

    public Npc(int x, int y, int id, CellType[][] level, Personality p) {
        super(x, y, id, level);
        rnd = new Random();
        rndDir = rnd.nextInt(1, 4);
        this.orignalPersonality=p;
        this.personality=p;
    }

    public void moveRandom(double speed) {

        // setDirection(rndDir);

        if (!moveInDirPossible(direction)) {
            rndDir = (int) Math.round(rnd.nextDouble(1, 4));
            setDirection(rndDir);
            changeDirection();
        }

        move(speed);
    }

    public void moveAstar(double speed, int[][] distField) {

        int bestDir = 0;
        int bestDist = Integer.MAX_VALUE;

        if (position.x > 0) {
            int v = distField[position.x - 1][position.y];
            if (v >= 0)
                if (v < bestDist) {
                    bestDist = v;
                    bestDir = 1;
                }
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

        if (bestDir != 0) {
            setDirection(bestDir);
        }
        changeDirection();
        move(speed);
    }

    public void moveAstarInv(double speed, int[][] distField) {

        int bestDir  = 0;
        int bestDist = -1;  // start below any reachable cell
    
        // LEFT (x-1, y) → dir=1
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
    
        // DOWN (x, y+1) → dir=2
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
    
        // RIGHT (x+1, y) → dir=3
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
    
        // UP (x, y-1) → dir=4
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
        changeDirection();
        move(speed);
    }


    void movePersonality(double speed, int thresh, int[][] distField) {

        switch (personality) {
            case Personality.CHASER -> moveChaser(speed, distField);
            case Personality.HEADLESSCHICKEN -> moveChicken(speed);
            case Personality.KEYBOARDWARRIOR -> moveKeyboardWarrior(speed, thresh, distField);
            case Personality.AGGRO -> moveAggro(speed, thresh, distField);
            case Personality.COWARD -> moveCoward(speed, distField);
            default -> {
            }
        }

        if(distField[position.x][position.y] == 0 )
            if (listener != null)
              listener.onCollision();

    }

    public void setListener(NpcListener l) {
        this.listener = l;
    }

    void moveChaser(double speed, int[][] distField) {
        moveAstar(speed, distField);

    }

    void moveCoward(double speed, int[][] distField) {
        moveAstarInv(speed, distField);

    }

    void moveChicken(double speed) {
        moveRandom(speed);
    }

    void moveAggro(double speed, int thresh, int[][] distField) {
        if (distField[position.x][position.y] < thresh)
            moveAstar(speed, distField);
        else
            moveRandom(speed);
    }

    void moveKeyboardWarrior(double speed, int thresh, int[][] distField) {
        if (distField[position.x][position.y] > thresh)
            moveAstar(speed, distField);
        else
            moveRandom(speed);
    }

    public void setPersonality(Personality p) {
        this.personality = p;
    }

    public Personality getPersonality() {
        return this.personality;
    }

    public void resetPersonality(){
        this.personality=this.orignalPersonality;
    }

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
