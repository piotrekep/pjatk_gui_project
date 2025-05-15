package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.Npc.NpcListener;


public class GameLogic implements NpcListener{

    private CellType[][] labirynt;
    private int[][] distanceField;
    private List<Point> npcSpawnPoints = new ArrayList<>();
    private final Map<Integer, Agent> agentList = new HashMap<>();
    private int maxPoints;
    private int x, y;
    public int level = 0;

    public interface GameLogicListener {
        void onVictory();
        void onDeath(int lives);
    }

    private GameLogicListener listener;

    public GameLogic(int x, int y) {
        this.x = x;
        this.y = y;
        maxPoints = 0;
        generateLevel();
        //calcDistanceField(3, 3);
        // fillWithPoints(this.labirynt);
        this.labirynt[3][3] = CellType.EMPTY;
        Player agent = SpawnPlayer(3, 3, 0);
        if (agent != null)
            agentList.put(0, agent);
        
        for(int i=1; i<= npcSpawnPoints.size(); i++){
        Npc npc = SpawnNpc(i);
        if (npc != null)
            agentList.put(i, npc);
        }
    }

    public void generateLevel() {
        Labirynth lab = new Labirynth(x, y);
        lab.generate();

        this.labirynt = lab.labirynt;
        fillWithPoints(this.labirynt);

        for (int i = 0; i < labirynt.length; i++)
            for (int j = 0; j < labirynt[0].length; j++) {
                if (labirynt[i][j] == CellType.GHOSTFLOOR) {
                    npcSpawnPoints.add(new Point(i, j));
                }
            }
        Player player = (Player) agentList.get(0);
        if (player != null) {
            this.labirynt[3][3] = CellType.EMPTY;
            player.updateLevel(this.labirynt);
            player.setPosition(3, 3);
            player.stopPlayer();
            maxPoints--;
        }
        calcDistanceField();
        
        
        for(int i=0; i< npcSpawnPoints.size(); i++){
        Npc npc = (Npc) agentList.get(i);
        if (npc != null) {
            npc.updateLevel(this.labirynt);
            reSpawnNpc(npc);
        }
        }
        level++;
    }

    public void resetLevel(){
        for(Agent agent : agentList.values()){
            agent.moveToSpawn();
        }

    }

    public void updatePlayer(boolean up, boolean down, boolean left, boolean right, double speed) {

        Player player = (Player) agentList.get(0);
        if (up || down || left || right) {
            if (up)
                player.setDirection(1);
            if (down)
                player.setDirection(3);
            if (left)
                player.setDirection(4);
            if (right)
                player.setDirection(2);
        } else {

        }
        player.move(speed);
        //calcDistanceField();
        if (listener != null && player.getPoints() >= maxPoints)
            listener.onVictory();

        //if (listener != null && player.collisionCheck())
        //    listener.onDeath(player.getLives());
    }

    public void updateNpc(double speed, int id,boolean powerup) {
        Npc npc = (Npc) agentList.get(id);
        if (npc != null)
            //npc.moveRandom(speed);
            if(distanceField!=null)
            //npc.moveAstar(speed,distanceField);
            if(powerup)
                npc.setPersonality(Personality.COWARD);
            else
                npc.setPersonality(Personality.CHASER);
            
            npc.movePersonality(speed,10,distanceField);

    }

    public void updateAllNpcs(double speed,boolean powerup){
        for(int i=1; i<=npcSpawnPoints.size(); i++){
            updateNpc(speed, i, powerup);
        }

    }

    public CellType[][] getGameState() {

        CellType[][] gameState = new CellType[labirynt.length][];
        for (int i = 0; i < labirynt.length; i++) {
            gameState[i] = labirynt[i].clone();
        }

        for (Agent agent : agentList.values()) {
            switch (agent) {
                case Player p -> gameState[p.position.x][p.position.y] = CellType.PLAYER;
                case Npc n -> gameState[n.position.x][n.position.y] = n.getCellType();
                default -> {
                }
            }
        }

        return gameState;

    }

    private Player SpawnPlayer(int x, int y, int id) {
        if (labirynt[x][y] == CellType.EMPTY) {
            Player player = new Player(x, y, id, labirynt);
            player.setLives(3);
            maxPoints--;
            return player;
        } else
            return null;
    }

    private Npc SpawnNpc(int id) {
        Random rand = new Random();
        Point spawn = npcSpawnPoints.get(rand.nextInt(npcSpawnPoints.size()));
        if (labirynt[spawn.x][spawn.y] != CellType.GHOSTFLOOR)
            return null;

        Npc npc = new Npc(spawn.x, spawn.y, id, labirynt);
        npc.setListener(this);
        return npc;
    }

    private Npc reSpawnNpc(Npc npc) {
        Random rand = new Random();
        Point spawn = npcSpawnPoints.get(rand.nextInt(npcSpawnPoints.size()));
        if (labirynt[spawn.x][spawn.y] != CellType.GHOSTFLOOR)
            return null;

        npc.setPosition(spawn.x, spawn.y);
        return npc;
    }

    private void fillWithPoints(CellType[][] level) {
        for (int i = 0; i < level.length; i++)
            for (int j = 0; j < level[0].length; j++)
                if (level[i][j] == CellType.EMPTY) {
                    level[i][j] = CellType.POINT;
                    maxPoints++;
                }
    }

    public int getPlayerScore(int id) {
        Player player = (Player) agentList.get(id);
        if (player != null) {
            return player.getPoints();
        } else
            return 0;
    }

    public int getLevelPoints() {
        return maxPoints;
    }

    public void setListener(GameLogicListener l) {
        this.listener = l;
    }

    private void step(int x, int y, int d) {
     
        if (x < 0 || x >= this.x || y < 0 || y >= this.y) return;
        if (labirynt[x][y] == CellType.WALL || labirynt[x][y] == CellType.GHOSTHOUSE) {
            if (distanceField[x][y] == 0)         
                distanceField[x][y] = -1;
            return;
        }
        if (labirynt[x][y] == CellType.GHOSTFLOOR)
            d=d+255;
      
        if (d >= distanceField[x][y]) return;
    
        distanceField[x][y] = d;      
    
        step(x + 1, y, d + 1);
        step(x - 1, y, d + 1);
        step(x, y + 1, d + 1);
        step(x, y - 1, d + 1);
    }

    public void calcDistanceFieldRec() {
    Player player = (Player) agentList.get(0);
    if (player == null) return;

    distanceField = new int[x][y];
    for (int[] row : distanceField) Arrays.fill(row, Integer.MAX_VALUE);
    step(player.position.x, player.position.y, 0);

    for (int x = 0; x < this.x; x++) {
        for (int y = 0; y < this.y; y++) {
            if (distanceField[x][y] == Integer.MAX_VALUE)
                distanceField[x][y] = -1;
        }
    }
}

public void calcDistanceField() {
    Player player = (Player) agentList.get(0);
    if (player == null) return;

      
    if (distanceField == null || distanceField.length != x || distanceField[0].length != y)
        distanceField = new int[x][y];

    for (int i = 0; i < x; i++) Arrays.fill(distanceField[i], Integer.MAX_VALUE);


    final int max = x * y;
    int[] qx = new int[max];        
    int[] qy = new int[max];        
    int head = 0, tail = 0;

    int sx = player.position.x;
    int sy = player.position.y;
    distanceField[sx][sy] = 0;
    qx[tail] = sx;
    qy[tail] = sy;
    tail++;

    final int[] dx = { 1, -1, 0, 0 };
    final int[] dy = { 0, 0, 1, -1 };

    while (head < tail) {
        int cx = qx[head];
        int cy = qy[head];
        head++;

        int nextDist = distanceField[cx][cy] + 1;

        for (int dir = 0; dir < 4; dir++) {
            int nx = cx + dx[dir];
            int ny = cy + dy[dir];

            if (nx < 0 || nx >= x || ny < 0 || ny >= y) continue;
            if (labirynt[nx][ny] == CellType.WALL || labirynt[nx][ny] == CellType.GHOSTHOUSE) continue;
            if (distanceField[nx][ny] <= nextDist) continue;   

            distanceField[nx][ny] = nextDist;  
            qx[tail] = nx;                      
            qy[tail] = ny;
            tail++;
        }
    }

    for (int i = 0; i < x; i++) {
        for (int j = 0; j < y; j++) {
            if (distanceField[i][j] == Integer.MAX_VALUE)
                distanceField[i][j] = -1;
        }
    }
}


    public int[][] getDistanceField() {
        return distanceField;
    }

    public Player getPlayer(int id){
        return (Player)agentList.get(id);
    }

    @Override
    public void onCollision() {
        if(listener!=null)
            listener.onDeath(level);
    }


}
