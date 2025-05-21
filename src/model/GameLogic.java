package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import model.Agent.AgentListener;


public class GameLogic implements AgentListener,
                        AgentModel{

    private CellType[][] labirynt;
    private int[][] distanceField;
    private List<Point> npcSpawnPoints = new ArrayList<>();
    private final Map<Integer, Agent> agentList = new ConcurrentHashMap<>();
    private int maxPoints;
    private int x, y;
    public int level = 0;
    private long powerupCooldown = System.nanoTime();
    

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

        this.labirynt[3][3] = CellType.EMPTY;
        Player agent = SpawnPlayer(3, 3, 0);
        if (agent != null)
            agentList.put(0, agent);
        
        //for(int i=1; i<= 1; i++){            
        for(int i=1; i<= npcSpawnPoints.size(); i++){
        Npc npc = SpawnNpc(i,intToPersonality(((i-1) % 5) + 1));
        if (npc != null){
           
            agentList.put(i, npc);
        }
        }
    }

    public void generateLevel() {
        resetLevel();
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
        
        
        for(int i=1; i<= npcSpawnPoints.size(); i++){
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
            if(agent.id<0)
                agentList.remove(agent.id);
            else
                agent.moveToSpawn();
        }
        powerupCooldown = System.nanoTime();
    }

    public void updatePlayer(boolean up, boolean down, boolean left, boolean right, double speed) {
        double speedMul=1;
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
        
        
    
            if(System.nanoTime()<player.pearlTime){
                player.powered=true;
            }
            else{
                player.powered=false;
            }
        
            if(System.nanoTime()<player.speedTime)
            {
                speedMul=1.5;
            }
            else{
                speedMul=1;
            }
                
        
        
        player.move(speed*speedMul);

        if (listener != null && player.getPoints() >= maxPoints)
            listener.onVictory();

        //if (listener != null && player.collisionCheck())
        //    listener.onDeath(player.getLives());
    }

    public void updateNpc(double speed, int id,boolean powerup) {
        Npc npc = (Npc) agentList.get(id);
        Player p = (Player)agentList.get(0);
        double speedMul=1;
        if (npc != null){
            if(distanceField!=null)
            if(p.powered){
                npc.setPersonality(Personality.COWARD);
                speedMul=0.75;
            }
            else{
                npc.resetPersonality();
                speedMul=1;
            }
                       
            npc.movePersonality(speed * speedMul,10,distanceField);
        }
    }

    public void updatePowerup(int id){
       if(id<0){ 
            Powerup powerup = (Powerup) agentList.get(id);
            if (powerup != null)
                if(distanceField!=null)
                    powerup.checkCollision(distanceField);
       }
    }

    public void updateAllPowerups(){
        for (int id : agentList.keySet()) {
            if(id<0){
                updatePowerup(id);
            }
        }
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
        //zmienianie tła tabeli, stary kod przed animacją
        // for (Agent agent : agentList.values()) {
        //     switch (agent) {
        //         case Player p -> gameState[p.position.x][p.position.y] = CellType.PLAYER;
        //         case Npc n -> gameState[n.position.x][n.position.y] = n.getCellType();
        //         case Powerup pw -> gameState[pw.position.x][pw.position.y] = CellType.POWERUP;
        //         default -> {
        //         }
        //     }
        // }

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

    private Npc SpawnNpc(int id, Personality personality) {
        Random rand = new Random();
        Point spawn = npcSpawnPoints.get(rand.nextInt(npcSpawnPoints.size()));
        if (labirynt[spawn.x][spawn.y] != CellType.GHOSTFLOOR)
            return null;

        Npc npc = new Npc(spawn.x, spawn.y, id, labirynt,personality);
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

    private void createPowerup(int x, int y,CellType[][] level){
        int id;     
        int minKey = Collections.min(agentList.keySet());
        id = minKey - 1;
        Powerup p = new Powerup(x,  y, id, level,  getRandPowerup());
        agentList.put(id, p);
        p.setListener(this);
    }

    private PowerupType getRandPowerup(){
        Random rand = new Random();
        double rng = rand.nextDouble(1);
        if(rng<0.1)
            return PowerupType.LIFE;
        else if(rng < 0.4)
            return PowerupType.PEARL;
        else if(rng < 0.6)
            return PowerupType.SPEED;
        else 
            return PowerupType.POINTS;
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
            return player.getTotalPoints();
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
    try{
    Player player = (Player) agentList.get(0);
    if (player == null) return;

    distanceField = new int[x][y];

    for (int i = 0; i < x; i++) Arrays.fill(distanceField[i], Integer.MAX_VALUE);


   
    int[] qx = new int[x * y];        
    int[] qy = new int[x * y];        
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

} catch (Exception e) {
   
    System.err.println("Błąd w calcDistanceField: " + e.getMessage());
    
     e.printStackTrace();
}
}
    Personality intToPersonality(int p){
        switch(p){
           case 1: return Personality.CHASER;
           case 2: return Personality.AGGRO;
           case 3: return Personality.KEYBOARDWARRIOR;
           case 4: return Personality.HEADLESSCHICKEN;
           case 5: return Personality.COWARD;
        }
        return Personality.CHASER;
    }


    public int[][] getDistanceField() {
        return distanceField;
    }

    public Player getPlayer(int id){
        return (Player)agentList.get(id);
    }



    @Override
    public void onCollision(Agent source) {
        Player player = (Player) agentList.get(0);

        if(listener!=null)
        if(source.id>0)
            if(!player.powered)
                listener.onDeath(level);
            else{
                player.addBonusPoints(100);
                source.moveToSpawn();
            }
        else{
            Powerup powerup = (Powerup)source;
            player.setPowerup(powerup.getPowerup()); 
            
            switch(player.getPowerup()){
                case PowerupType.LIFE -> player.setLives(player.getLives()+1);
                case PowerupType.POINTS -> player.addBonusPoints(powerup.getPowerup().getExtraPoints()); 
                case PowerupType.PEARL -> player.pearlTime=System.nanoTime() + (player.getPowerup().getDuration() * 1_000_000_000l) ;
                case PowerupType.SPEED -> player.speedTime=System.nanoTime() + (player.getPowerup().getDuration() * 1_000_000_000l);
                default ->{}
            }
            agentList.remove(powerup.id);

            }
    }

    @Override
    public void onChangePosition(Agent source) {
        if( System.nanoTime() - powerupCooldown > 5_000_000_000l){
            double rng=Math.random();
        if(rng<0.25){
            powerupCooldown = System.nanoTime();
            createPowerup(source.position.x,source.position.y,labirynt);

        }
        }
    }

    @Override
    public Collection<Agent> getAgents() {
        return Collections.unmodifiableCollection(agentList.values());
    }


}
