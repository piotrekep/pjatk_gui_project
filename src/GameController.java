import java.awt.event.KeyListener;

public class GameController implements  Runnable {
    private volatile boolean running;
    private KeyHandler keyhandler;
    private GameLogic gamelogic;
    private VisuController visucontroller;


    public GameController(KeyHandler keyhandler, GameLogic gamelogic, VisuController visucontroller ){
        this.keyhandler=keyhandler;
        this.gamelogic=gamelogic;
        this.visucontroller=visucontroller;

    }

    @Override
    public void run() {
        final int TARGET_FPS = 1;
        final long   OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS; // in nanoseconds
        running=true;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            long updateLength = now - lastTime;
            lastTime = now;

            gamelogic.updateGameState(keyhandler.up(),keyhandler.down(),keyhandler.left(),keyhandler.right());   
            gamelogic.updateTask();
           
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

    public KeyListener getKeyListener() {
        return keyhandler.getKeyListener();
    }
}
