package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameLogic {

    private CellType[][] labirynt;
    private List<Point> npcSpawnPoints = new ArrayList<>();
    private final Map<String, Agent> agentList = new HashMap<>();
    private int maxPoints;
    private int x, y;
    public int level=0;

    public interface GameLogicListener {
        void onVictory();
    }

    private GameLogicListener listener;

    public GameLogic(int x, int y) {
        this.x = x;
        this.y = y;
        maxPoints=0;
        generateLevel();

        // fillWithPoints(this.labirynt);
        this.labirynt[3][3] = CellType.EMPTY;
        Player agent = SpawnPlayer(3, 3, "player");
        if (agent != null)
            agentList.put(agent.name, agent);

        Npc npc = SpawnNpc("enemy");
            if(npc != null)
                agentList.put(npc.name, npc);

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
            Player player = (Player) agentList.get("player");
            if(player != null){
                this.labirynt[3][3] = CellType.EMPTY;
                player.updateLevel(this.labirynt);
                player.setPosition(3, 3);
                player.stopPlayer();
                maxPoints--;
            }

            Npc npc = (Npc) agentList.get("enemy");
            if(npc != null){
                npc.updateLevel(this.labirynt);
                reSpawnNpc(npc);
            }
            level++;
        }

    public void updatePlayer(boolean up, boolean down, boolean left, boolean right, double speed) {

        Player player = (Player) agentList.get("player");
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
        if (listener != null && player.getPoints() >= maxPoints)
            listener.onVictory();
    }

    public void updateNpc(double speed, String name){
        Npc npc = (Npc) agentList.get(name);
       if(npc!=null)
        npc.moveRandom(speed);
    }

    public CellType[][] getGameState() {

        CellType[][] gameState = new CellType[labirynt.length][];
        for (int i = 0; i < labirynt.length; i++) {
            gameState[i] = labirynt[i].clone();
        }

        for (Agent agent : agentList.values()) {
            switch (agent) {
                case Player p -> gameState[p.position.x][p.position.y] = CellType.PLAYER;
                case Npc n -> gameState[n.position.x][n.position.y] = CellType.NPC1;
                default -> {
                }
            }
        }

        return gameState;

    }

    private Player SpawnPlayer(int x, int y, String name) {
        if (labirynt[x][y] == CellType.EMPTY) {
            Player player = new Player(x, y, name, labirynt);
            maxPoints--;
            return player;
        } else
            return null;
    }


    private Npc SpawnNpc(String name){
        Random rand = new Random();
        Point spawn= npcSpawnPoints.get(rand.nextInt(npcSpawnPoints.size()));
        if(labirynt[spawn.x][spawn.y] != CellType.GHOSTFLOOR)
            return null;

        Npc npc = new Npc(spawn.x,spawn.y,name,labirynt);
        return npc;
    }


    private Npc reSpawnNpc(Npc npc){
        Random rand = new Random();
        Point spawn= npcSpawnPoints.get(rand.nextInt(npcSpawnPoints.size()));
        if(labirynt[spawn.x][spawn.y] != CellType.GHOSTFLOOR)
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

    public int getPlayerScore(String name) {
        Player player = (Player) agentList.get(name);
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

}
