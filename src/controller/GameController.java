package controller;

import model.CellType;
import model.GameLogic;

import java.awt.event.KeyListener;

import visual.DifficultyView;
import visual.GameView;
import visual.MenuView;
import visual.ScoreView;
import visual.CellTypeVisu;


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
            
            gamelogic.updatePlayer(keyhandler.up(),keyhandler.down(),keyhandler.left(),keyhandler.right());   
            
            this.game.updateLevel(stateToVisu(gamelogic.getGameState()));
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

    private CellTypeVisu[][] stateToVisu(CellType[][] board){
        CellTypeVisu[][] temp = new CellTypeVisu[board.length][board[0].length];
        for(int i=0; i<temp.length;i++)
            for(int j=0; j<temp[0].length;j++){
                switch (board[i][j]) {
                    case CellType.EMPTY -> temp[i][j]=CellTypeVisu.EMPTY;
                    case CellType.WALL -> temp[i][j]=CellTypeVisu.WALL;
                    case CellType.PLAYER -> temp[i][j]=CellTypeVisu.PLAYER;
                    case CellType.NPC1 -> temp[i][j]=CellTypeVisu.NPC1;
                    case CellType.NPC2 -> temp[i][j]=CellTypeVisu.NPC2;
                    case CellType.GHOSTHOUSE -> temp[i][j]=CellTypeVisu.GHOSTHOUSE;
                    case CellType.GHOSTFLOOR -> temp[i][j]=CellTypeVisu.GHOSTFLOOR;
                    default -> temp[i][j] = CellTypeVisu.EMPTY;
                    }
            }
        return temp;
        
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

        gamelogic = new GameLogic(x,y);

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
