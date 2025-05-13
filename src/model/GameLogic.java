package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameLogic {

    private CellType[][] labirynt;
    private int[][] distanceField;
    private List<Point> npcSpawnPoints = new ArrayList<>();
    private final Map<String, Agent> agentList = new HashMap<>();
    private int maxPoints;
    private int x, y;
    public int level = 0;

    public interface GameLogicListener {
        void onVictory();
    }

    private GameLogicListener listener;

    public GameLogic(int x, int y) {
        this.x = x;
        this.y = y;
        maxPoints = 0;
        generateLevel();

        // fillWithPoints(this.labirynt);
        this.labirynt[3][3] = CellType.EMPTY;
        Player agent = SpawnPlayer(3, 3, "player");
        if (agent != null)
            agentList.put(agent.name, agent);

        Npc npc = SpawnNpc("enemy");
        if (npc != null)
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
        if (player != null) {
            this.labirynt[3][3] = CellType.EMPTY;
            player.updateLevel(this.labirynt);
            player.setPosition(3, 3);
            player.stopPlayer();
            maxPoints--;
        }

        Npc npc = (Npc) agentList.get("enemy");
        if (npc != null) {
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
        calcDistanceField(player.position.x, player.position.y);
        if (listener != null && player.getPoints() >= maxPoints)
            listener.onVictory();
    }

    public void updateNpc(double speed, String name) {
        Npc npc = (Npc) agentList.get(name);
        if (npc != null)
            //npc.moveRandom(speed);
            npc.moveAstar(speed,distanceField);
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

    private Npc SpawnNpc(String name) {
        Random rand = new Random();
        Point spawn = npcSpawnPoints.get(rand.nextInt(npcSpawnPoints.size()));
        if (labirynt[spawn.x][spawn.y] != CellType.GHOSTFLOOR)
            return null;

        Npc npc = new Npc(spawn.x, spawn.y, name, labirynt);
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

    private void step(int x, int y, int d) {
        // 1) poza mapą lub ściana – wracamy
        if (x < 0 || x >= this.x || y < 0 || y >= this.y) return;
        if (labirynt[x][y] == CellType.WALL || labirynt[x][y] == CellType.GHOSTHOUSE) {
            if (distanceField[x][y] == 0)          // 0 = niezainicjowane
                distanceField[x][y] = -1;
            return;
        }
        if (labirynt[x][y] == CellType.GHOSTFLOOR){
            distanceField[x][y]=255+d;
            return;
        }
    
        // 2) znaleźliśmy już krótszą drogę ⇒ nie schodzimy głębiej
        if (d >= distanceField[x][y]) return;
    
        distanceField[x][y] = d;      // zapisz lepszy wynik
    
        // 3) rekurencja w cztery strony (głębiej o 1)
        step(x + 1, y, d + 1);
        step(x - 1, y, d + 1);
        step(x, y + 1, d + 1);
        step(x, y - 1, d + 1);
    }

void calcDistanceField(int ox, int oy) {
    distanceField = new int[x][y];
    for (int[] row : distanceField) Arrays.fill(row, Integer.MAX_VALUE);
    step(ox, oy, 0);

    for (int x = 0; x < this.x; x++) {
        for (int y = 0; y < this.y; y++) {
            if (distanceField[x][y] == Integer.MAX_VALUE)
                distanceField[x][y] = -1;
        }
    }
}


    public int[][] getDistanceField() {
        return distanceField;
    }
}
