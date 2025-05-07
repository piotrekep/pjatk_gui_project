import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.*;

public class GameLogic implements KeyListener, Runnable{
    private GameBoard board;
    private CellType[][] labirynt;
    private CellType[][] levelState;
    private List<Point> npcSpawnPoints = new ArrayList<>();
    private volatile boolean up, down, left, right;
    private volatile boolean running;
    private final Map<String, Agent> agentList = new HashMap<>();

    public GameLogic(GameBoard board){
        this.board=board;
        this.labirynt=this.board.getBoard();
        int rows = labirynt.length;
        int cols = labirynt[0].length;      
        this.levelState = new CellType[rows][cols];

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
            //levelState[x][y]=CellType.PLAYER;
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

            
            if (up || down || left || right) {
                if (up)    agentList.get("player").setDirection(1);
                if (down)  agentList.get("player").setDirection(3);
                if (left)  agentList.get("player").setDirection(4);
                if (right) agentList.get("player").setDirection(2);
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



    public void stop() {
        running = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:    up  = true; break;
            case KeyEvent.VK_S:  down  = true; break;
            case KeyEvent.VK_A:  left  = true; break;
            case KeyEvent.VK_D: right  = true; break;
            case KeyEvent.VK_UP:    up  = true; break;
            case KeyEvent.VK_DOWN:  down  = true; break;
            case KeyEvent.VK_LEFT:  left  = true; break;
            case KeyEvent.VK_RIGHT: right  = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:    up  = false; break;
            case KeyEvent.VK_S:  down  = false; break;
            case KeyEvent.VK_A:  left  = false; break;
            case KeyEvent.VK_D: right = false; break;
            case KeyEvent.VK_UP:    up  = false; break;
            case KeyEvent.VK_DOWN:  down  = false; break;
            case KeyEvent.VK_LEFT:  left  = false; break;
            case KeyEvent.VK_RIGHT: right = false; break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println(agentList.get("player").position.x + " " + agentList.get("player").position.x );
        //throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    public KeyListener getKeyListener() {
        return this;
    }

}
