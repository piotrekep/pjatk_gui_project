package controller;

import model.GameLogic;

import java.awt.event.KeyListener;

import visual.DifficultyView;
import visual.GameView;
import visual.MenuView;
import visual.ScoreView;



public class GameController implements  Runnable,
                                        MenuView.MenuListener,
                                        ScoreView.ScoreListener,
                                        DifficultyView.DifficultyListener,
                                        GameView.GameListener {

    private volatile boolean running;
    private KeyHandler keyhandler;
    public GameLogic gamelogic;

    private MenuView menu;
    private ScoreView score;
    private DifficultyView difficulty;
    private GameView game;



    public GameController(KeyHandler keyhandler,
                            MenuView menu,
                            ScoreView score,
                            DifficultyView difficulty,
                            GameView game ){
        this.keyhandler=keyhandler;
        this.menu=menu;
        this.score=score;
        this.difficulty=difficulty;
        this.game=game;

        this.menu.setListener(this);
        this.score.setListener(this);
        this.difficulty.setListener(this);

        this.game.setListener(this);
        this.game.setKeyListener(keyhandler);


        this.menu.setVisible(true);
    }

    @Override
    public void run() {
        //final int TARGET_FPS = 1;
        //final long   OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS; // in nanoseconds
        running=true;
        //long lastTime = System.nanoTime();

        while (running) {
            //ong now = System.nanoTime();
            //long updateLength = now - lastTime;
            //lastTime = now;
            
            gamelogic.updateGameState(keyhandler.up(),keyhandler.down(),keyhandler.left(),keyhandler.right());   
            gamelogic.updateTask();
           
            //long sleepTime = (OPTIMAL_TIME - (System.nanoTime() - now)) / 1_000_000;
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
        gamelogic = new GameLogic(game.getBoard());

        game.setVisible(true);
        difficulty.setVisible(false);

        running = true;
        new Thread(this, "GameLoopThread").start();

    }

    @Override
    public void onCloseGameWindow() {
        stop(); 
        game.setVisible(false);
        menu.setVisible(true);
    }
}
