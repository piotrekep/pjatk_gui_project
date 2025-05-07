import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.*;

public class GameLogic implements  Runnable{
    private GameBoard board;
    private CellType[][] labirynt;
    private List<Point> npcSpawnPoints = new ArrayList<>();
    private volatile boolean running;
    private final Map<String, Agent> agentList = new HashMap<>();

    private KeyHandler keyhandler = new KeyHandler();

    public GameLogic(GameBoard board){
        this.board=board;
        this.labirynt=this.board.getBoard();

        for(int i = 0; i<labirynt.length;i++)
            for(int j = 0; j<labirynt[0].length;j++){
                if(labirynt[i][j]==CellType.GHOSTFLOOR){
                    npcSpawnPoints.add(new Point(i,j));
                }
            }
            Player agent = SpawnPlayer(3,3,"player");
            if(agent != null)
                agentList.put(agent.name, agent);
            running = true;
            
    }


    private void updateTask() {
   
       CellType[][] frameBuffer = new CellType[labirynt.length][];
       for (int i = 0; i < labirynt.length; i++) {
           frameBuffer[i] = labirynt[i].clone();      
       }
     
       
        for (Agent agent : agentList.values()) {
            switch (agent) {
                case Player p -> frameBuffer[p.position.x][p.position.y] = CellType.PLAYER;
                case Npc n -> frameBuffer[n.position.x][n.position.y] = CellType.NPC1;
                default -> {}
            }
        }
        
       
        this.board.setBoard(frameBuffer);
        
    }


    private Player SpawnPlayer(int x, int y, String name)
    {
        if(labirynt[x][y]==CellType.EMPTY){
            Player player = new Player(x,y, name,labirynt);
            return player;
        }
        else return null;
    }

    @Override
    public void run() {
        final int TARGET_FPS = 1;
        final long   OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS; // in nanoseconds

        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            long updateLength = now - lastTime;
            lastTime = now;

            
            if (keyhandler.up() || keyhandler.down() || keyhandler.left() || keyhandler.right()) {
                if (keyhandler.up())    agentList.get("player").setDirection(1);
                if (keyhandler.down())  agentList.get("player").setDirection(3);
                if (keyhandler.left())  agentList.get("player").setDirection(4);
                if (keyhandler.right()) agentList.get("player").setDirection(2);
            } else {
                
            }
            agentList.get("player").move();
           

            updateTask();
            long sleepTime = (OPTIMAL_TIME - (System.nanoTime() - now)) / 1_000_000;
            //if (sleepTime > 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    
                    if (!running) break;
                }
            //}
        }
    }

    public KeyListener getKeyListener() {
        return keyhandler.getKeyListener();
    }

    public void stop() {
        running = false;
    }



}
