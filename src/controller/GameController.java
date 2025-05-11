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

    private final int TARGET_FPS = 25;
    private final long OPTIMAL_TIME;

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

        this.OPTIMAL_TIME = 1_000_000_000L /  this.TARGET_FPS ;

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
            long now = System.nanoTime();
            long elapsedNanos = now - lastTime;
            lastTime = now;
            double delta = (double) elapsedNanos / OPTIMAL_TIME;


            
            gamelogic.updatePlayer(
                keyhandler.up(),
                keyhandler.down(),
                keyhandler.left(),
                keyhandler.right(),
                5
            );
            game.setScore(gamelogic.getPlayerScore("player"));
            
            this.game.updateLevel(
                stateToVisu(gamelogic.getGameState())
            );
           
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
                    case CellType.POINT -> temp[i][j]=CellTypeVisu.POINT;
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
