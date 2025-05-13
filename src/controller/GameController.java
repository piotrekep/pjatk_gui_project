package controller;

import model.CellType;
import model.GameLogic;
import model.GameLogic.GameLogicListener;

import java.awt.event.KeyListener;

import javax.swing.JOptionPane;

import visual.DifficultyView;
import visual.GameView;
import visual.MenuView;
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

    private MenuView menu;
    private ScoreView score;
    private DifficultyView difficulty;
    private GameView game;

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
            // double delta = (double) elapsedNanos / OPTIMAL_TIME;

            gamelogic.updatePlayer(
                    keyhandler.up(),
                    keyhandler.down(),
                    keyhandler.left(),
                    keyhandler.right(),
                    5);
            game.setScore(gamelogic.getPlayerScore("player"));

            gamelogic.updateNpc(3, "enemy");

            this.game.updateLevel(
                    stateToVisu(gamelogic.getGameState()));

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

    private CellTypeVisu[][] stateToVisu(CellType[][] board) {
        CellTypeVisu[][] temp = new CellTypeVisu[board.length][board[0].length];
        for (int i = 0; i < temp.length; i++)
            for (int j = 0; j < temp[0].length; j++) {
                switch (board[i][j]) {
                    case CellType.EMPTY -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.EMPTY);
                    case CellType.WALL -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.WALL);
                    case CellType.PLAYER -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.PLAYER);
                    case CellType.NPC1 -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.NPC1);
                    case CellType.NPC2 -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.NPC2);
                    case CellType.GHOSTHOUSE -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.GHOSTHOUSE);
                    case CellType.GHOSTFLOOR -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.GHOSTFLOOR);
                    case CellType.POINT -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.POINT);
                    default -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.EMPTY);
                }
                
                temp[i][j].val=gamelogic.getDistanceField()[i][j];
            }
        return temp;

    }

    public void stop() {
        running = false;
        // obudź, żeby nie wisiał w pauzie
        resume();
    }

    /** Wejdź w stan pauzy (wątek idzie do wait()) */
    public void pause() {
        paused = true;
    }

    /** Wznów pracę wątku, jeśli wcześniej było pauzowane */
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
        game.createLevel(x, y);

        gamelogic = new GameLogic(x, y);
        this.gamelogic.setListener(this);

        game.setVisible(true);
        difficulty.setVisible(false);

        running = true;
        gameLoopThread = new Thread(this, "GameLoopThread");
        gameLoopThread.start();

    }

    @Override
    public void onCloseGameWindow() {
        stop();
        gameLoopThread.interrupt();
        try {
            gameLoopThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        game.setVisible(false);
        menu.setVisible(true);
    }

    @Override
    public void onVictory() {
        pause();
        JOptionPane.showMessageDialog(game, "Level: " + gamelogic.level, "Victory!", JOptionPane.INFORMATION_MESSAGE);
        gamelogic.generateLevel();
        keyhandler.clear();
        resume();

    }
}
