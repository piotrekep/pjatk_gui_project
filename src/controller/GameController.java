package controller;

import model.CellType;
import model.GameLogic;
import model.GameLogic.GameLogicListener;

import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import visual.DifficultyView;
import visual.GameView;
import visual.MenuView;
import visual.PlayerScore;
import visual.ScoreView;
import visual.CellTypeVisu;

public class GameController implements Runnable,
        MenuView.MenuListener,
        ScoreView.ScoreListener,
        DifficultyView.DifficultyListener,
        GameView.GameListener,
        GameLogicListener {

    private Thread gameLoopThread;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private volatile boolean running;
    private KeyHandler keyhandler;
    public GameLogic gamelogic;

    private final CyclicBarrier frameBarrier = new CyclicBarrier(2);
    private Thread npcThread;

    private MenuView menu;
    private ScoreView score;
    private DifficultyView difficulty;
    private GameView game;
    private SerializableList<PlayerScore> highScoreList;

    private final int TARGET_FPS = 30;
    private final long OPTIMAL_TIME;
            
    

    public GameController(KeyHandler keyhandler,
            MenuView menu,
            ScoreView score,
            DifficultyView difficulty,
            GameView game) {
        this.keyhandler = keyhandler;
        this.menu = menu;
        this.score = score;
        this.difficulty = difficulty;
        this.game = game;

        this.OPTIMAL_TIME = 1_000_000_000L / this.TARGET_FPS;

        this.menu.setListener(this);
        this.score.setListener(this);
        this.difficulty.setListener(this);
       

        try {
            highScoreList = SerializableList.loadFromFile("highscores.list");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (PlayerScore playerScore : highScoreList) {
            score.addHighScore(playerScore.getName(),playerScore.getScore());
        }

        this.game.setListener(this);
        this.game.setKeyListener(keyhandler);

        this.menu.setVisible(true);
    }

    @Override
    public void run() {
        running = true;
        long lastTime = System.nanoTime();

        while (running) {

            synchronized (pauseLock) {
                while (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            long now = System.nanoTime();
            long elapsedNanos = now - lastTime;
            lastTime = now;
            double delta = (double) elapsedNanos / OPTIMAL_TIME;

            gamelogic.updatePlayer(
                    keyhandler.up(),
                    keyhandler.down(),
                    keyhandler.left(),
                    keyhandler.right(),
                    5);

            try {
                frameBarrier.await(); //obliczenia ruchu npc mogą się wykonać dopiero po zaktualizowaniu opołożenia gracza

                
                frameBarrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                Thread.currentThread().interrupt();
                return;
            }

            game.setScore(gamelogic.getPlayerScore(0));
            game.setLives(gamelogic.getPlayer(0).getLives());


            long frameTime = System.nanoTime() - now;
            long sleepTimeMs = (OPTIMAL_TIME - frameTime) / 1_000_000L;

            if (sleepTimeMs > 0) {
                try {
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private Runnable npcLoop() {
        return () -> {
            try {
                while (running) {

                    
                    frameBarrier.await(); //położenie gracza zaktualizowane można obliczać a-star
            
                    gamelogic.calcDistanceField();            
                    frameBarrier.await();
                    gamelogic.updateAllNpcs(3, keyhandler.test());
                    gamelogic.updateAllPowerups();
                    CellTypeVisu[][] frame = stateToVisu(gamelogic.getGameState());
                    SwingUtilities.invokeLater(() -> game.updateLevel(frame));

                }
            } catch (InterruptedException | BrokenBarrierException ex) {
                Thread.currentThread().interrupt();
            }
        };
    }

    private CellTypeVisu[][] stateToVisu(CellType[][] board) {
        CellTypeVisu[][] temp = new CellTypeVisu[board.length][board[0].length];
        for (int i = 0; i < temp.length; i++)
            for (int j = 0; j < temp[0].length; j++) {
                switch (board[i][j]) {
                    case CellType.EMPTY -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.EMPTY);
                    case CellType.WALL -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.WALL);
                    case CellType.PLAYER -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.PLAYER);
                    case CellType.NPC_CHASER -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.NPC_CHASER);
                    case CellType.NPC_AGGRO -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.NPC_AGGRO);
                    case CellType.NPC_KEYBOARDWARRIOR -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.NPC_KEYBOARDWARRIOR);
                    case CellType.NPC_HEADLESSCHICKEN -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.NPC_HEADLESSCHICKEN);
                    case CellType.NPC_COWARD -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.NPC_COWARD);
                    case CellType.GHOSTHOUSE -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.GHOSTHOUSE);
                    case CellType.GHOSTFLOOR -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.GHOSTFLOOR);
                    case CellType.POINT -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.POINT);
                    case CellType.POWERUP -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.POWERUP);
                    default -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.EMPTY);
                }

                temp[i][j].val = gamelogic.getDistanceField()[i][j];
            }
        return temp;

    }

    public void stop_old() {
        running = false;
        resume();
    }

    public void stop() {
        running = false;
        resume(); 
        npcThread.interrupt();
        frameBarrier.reset();
        try {
            npcThread.join();
            gameLoopThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public KeyListener getKeyListener() {
        return keyhandler.getKeyListener();
    }

    @Override
    public void onNewGame() {
        menu.setVisible(false);
        difficulty.setVisible(true);
    }

    @Override
    public void onShowScores() {
        score.setVisible(true);
        menu.setVisible(false);
    }

    @Override
    public void onExit() {
        System.exit(0);
    }

    @Override
    public void onCloseScoreWindow() {
        score.setVisible(false);
        menu.setVisible(true);
    }

    @Override
    public void onCloseDificultyWindow() {
        menu.setVisible(true);
        difficulty.setVisible(false);
    }

    @Override
    public void onStartGame(int x, int y) {
        

        gamelogic = new GameLogic(x, y);
        this.gamelogic.setListener(this);

        game.setAgentModel(gamelogic);
        game.createLevel(x, y);

        game.setVisible(true);
        game.getAnimatedTable().startAnimation();
        
        
        difficulty.setVisible(false);

        running = true;
        gameLoopThread = new Thread(this, "GameLoopThread");
        npcThread = new Thread(npcLoop(), "NPC-Thread");

        gameLoopThread.start();
        npcThread.start();
    }

    @Override
    public void onCloseGameWindow() {
        stop();
        game.getAnimatedTable().stopAnimation();
        SwingUtilities.invokeLater(() -> {
            game.setVisible(false);
            menu.setVisible(true);
          });
    }

    @Override
    public void onVictory() {
        pause();
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(game, "Level: " + gamelogic.level + " compleated!", "Victory!", JOptionPane.INFORMATION_MESSAGE)
        );
        gamelogic.generateLevel();
        keyhandler.clear();
        resume();

    }

    @Override
    public void onDeath(int lives) {
        pause();
        if (gamelogic.getPlayer(0).getLives() > 0) {
            
            gamelogic.getPlayer(0).setLives(gamelogic.getPlayer(0).getLives()-1);

            
            JOptionPane.showMessageDialog(game, "You Died","You Died", JOptionPane.INFORMATION_MESSAGE );
            

            gamelogic.resetLevel();
            keyhandler.clear();
            resume();
        }
        else{
            String newName = JOptionPane.showInputDialog(null,"Game Over! Score: "+gamelogic.getPlayerScore(0),"Player");

            PlayerScore pscore= new PlayerScore(newName, gamelogic.getPlayerScore(0));
            if(pscore!=null)
                highScoreList.add(pscore);
            score.addHighScore(pscore.getName(), pscore.getScore());

            try {
                highScoreList.saveToFile("highscores.list");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
                // SwingUtilities.invokeLater(() ->
                //      game.dispatchEvent(new WindowEvent(game, WindowEvent.WINDOW_CLOSING))
                //  );
            keyhandler.clear();
            onCloseGameWindow();

        }

    }
}
