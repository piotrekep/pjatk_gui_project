package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogic {

    private CellType[][] labirynt;
    private List<Point> npcSpawnPoints = new ArrayList<>();
    private final Map<String, Agent> agentList = new HashMap<>();
    private int maxPoints;

    public GameLogic(int x, int y) {

        Labirynth lab = new Labirynth(x, y);
        lab.generate();
        maxPoints=0;

        this.labirynt = lab.labirynt;
        fillWithPoints(this.labirynt);

        for (int i = 0; i < labirynt.length; i++)
            for (int j = 0; j < labirynt[0].length; j++) {
                if (labirynt[i][j] == CellType.GHOSTFLOOR) {
                    npcSpawnPoints.add(new Point(i, j));
                }
            }
        //fillWithPoints(this.labirynt);
        this.labirynt[3][3]=CellType.EMPTY; 
        Player agent = SpawnPlayer(3, 3, "player");
        if (agent != null)
            agentList.put(agent.name, agent);

    }

    public void updatePlayer(boolean up, boolean down, boolean left, boolean right, double speed) {

        if (up || down || left || right) {
            if (up)
                agentList.get("player").setDirection(1);
            if (down)
                agentList.get("player").setDirection(3);
            if (left)
                agentList.get("player").setDirection(4);
            if (right)
                agentList.get("player").setDirection(2);
        } else {

        }
        Player player = (Player) agentList.get("player");
        player.move(speed);
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
            return player;
        } else
            return null;
    }

    private void fillWithPoints(CellType[][] level) {
        for (int i = 0; i < level.length; i++)
            for (int j = 0; j < level[0].length; j++)
                if (level[i][j] == CellType.EMPTY){
                    level[i][j] = CellType.POINT;
                    maxPoints++;
                }
    }

    public int getPlayerScore(String name){
        Player player = (Player)agentList.get(name);
        if(player !=null){
            return player.getPoints();
        }
        else
            return 0;
    }

}
