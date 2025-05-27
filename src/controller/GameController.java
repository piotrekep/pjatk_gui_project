package controller;

import model.CellType;
import model.GameLogic;
import model.GameLogic.GameLogicListener;

import java.awt.event.KeyListener;
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



/**
 * @class GameController
 * @brief Implementuje Kontroler do modelu MVC
 *
 *       Klasa implementuje funkcjonalność kontrolera w modelu MVC.
 *       Aplikacja jest na tyle prosta, że logikę działania można zawrzeć w jednej klasie
 */


public class GameController implements Runnable,
        MenuView.MenuListener,
        ScoreView.ScoreListener,
        DifficultyView.DifficultyListener,
        GameView.GameListener,
        GameLogicListener {
/**Główny wątek gry*/
    private Thread gameLoopThread;
    /** wstrzymanie */
    private volatile boolean paused = false;
    /** obiekt synchronizujący wątki na potrzeby wstrzymania */
    private final Object pauseLock = new Object();
    /** zmienna running do nieskończonej pętly w wątkach */
    private volatile boolean running;
    /** obsługa przycisków */
    private KeyHandler keyhandler;
    /** obsłyuga logiki gry */
    public GameLogic gamelogic;
/** bariera do synchronizacji wątków */
    private final CyclicBarrier frameBarrier = new CyclicBarrier(2);
    /** wątek obsługujący "AI" przeciwników */
    private Thread npcThread;
/** menu główne */
    private MenuView menu;
    /** lista wyników */
    private ScoreView score;
    /** menu wyboru wielkości labiryntu */
    private DifficultyView difficulty;
    /** okno planszy gry */
    private GameView game;
    /** lista wyników */
    private SerializableList<PlayerScore> highScoreList;
/** dolcelowa liczba klatek na sekunde "fizyki" */
    private final int TARGET_FPS = 30;
    /** optymalny czas każdej klatki */
    private final long OPTIMAL_TIME;
            
    
/** konstruktor */
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
/** obliczenia optymalnego czasu klatki w nanosekundach */
        this.OPTIMAL_TIME = 1_000_000_000L / this.TARGET_FPS;

        this.menu.setListener(this);
        this.score.setListener(this);
        this.difficulty.setListener(this);
       
/** ładowanie listy wyników zapisanej w pliku */
        try {
            highScoreList = SerializableList.loadFromFile("highscores.list");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
/** ładowanie listy wyników do listy roboczej */
        for (PlayerScore playerScore : highScoreList) {
            score.addHighScore(playerScore.getName(),playerScore.getScore());
        }

        this.game.setListener(this);
        this.game.setKeyListener(keyhandler);
        /** pokazanie głównego menu gry */
        this.menu.setVisible(true);
    }

    /**Watek logiki gry */
    @Override
    public void run() {
        running = true;
        long lastTime = System.nanoTime();

        while (running) {
            /** synchronizacja pauzy wątków */
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
                /** bariera - ruch gracza został wykonany. oczekujemy na ruch AI */
                frameBarrier.await(); 

                /** ruch AI wykonany, można wznowić wątek */
                frameBarrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                Thread.currentThread().interrupt();
                return;
            }

            game.setScore(gamelogic.getPlayerScore(0));
            game.setLives(gamelogic.getPlayer(0).getLives());

            /** obliczenia na jak długo trzeba zatrzymać wątek, aby utrzymać 30fps */
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

/** wątek NPC */
    private Runnable npcLoop() {
        return () -> {
            try {
                while (running) {

                    
                    frameBarrier.await(); 
            
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

    /** konwerter typu pola z logicznego na graficzny
    * @param board plansza gry w formie logiki
    * @return odpowiednik planszy w formie wizualnej
    */
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
                    case CellType.NPC_POWERUP -> temp[i][j] = new CellTypeVisu(CellTypeVisu.Type.NPC_POWERUP);
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

/**
 * metoda zatrzymująca wątki gry
 */
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

    /**
     *  pauzowanie wątków
     */
    public void pause() {
        paused = true;
    }

    /**
     *  wznawianie wątków
     */
    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    /**
     * metoda zwracająca listener przycisków
     * @return
     */
    public KeyListener getKeyListener() {
        return keyhandler.getKeyListener();
    }

    /**
     *  metoda wywoływana przez listenera przy kliknięciu przycisku nowa gra
     */
    @Override
    public void onNewGame() {
        menu.setVisible(false);
        difficulty.setVisible(true);
    }

    /**
     *  metoda wywoływana przez listenera przy kliknięciu przycisku pokazania wyników
     */
    @Override
    public void onShowScores() {
        score.setVisible(true);
        menu.setVisible(false);
    }
    /**
     *  metoda wywoływana przez listenera przy kliknięciu przycisku exit
     */
    @Override
    public void onExit() {
        System.exit(0);
    }
    /**
     *  metoda wywoływana przez listenera przycisku zamknięcia okna wyników 
     */
    @Override
    public void onCloseScoreWindow() {
        score.setVisible(false);
        menu.setVisible(true);
    }
    /**
     *  metoda wywoływana przez listenera przycisku zamknięcia okna wyboru rozmiaru planszy
     */
    @Override
    public void onCloseDificultyWindow() {
        menu.setVisible(true);
        difficulty.setVisible(false);
    }
    /**
     *  metoda wywoływana przez listenera przycisku rozpoczęcia gry
     * @param x rozmiar x
     * @param y rozmiar y
     * inicjuje gre i uruchamia wątki
     */
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
    /**
     *  metoda wywoływana przez listenera zamknięcia okna gry
     * zatrzymuje wątki i animacje
     */
    @Override
    public void onCloseGameWindow() {
        stop();
        game.getAnimatedTable().stopAnimation();
        SwingUtilities.invokeLater(() -> {
            game.setVisible(false);
            menu.setVisible(true);
          });
    }
    /**
     *  metoda wywoływana przez listenera wygranej poziomu
     * wstrzymuje wątki i ponownie inicjalizuje level
     */
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
    /**
     *  metoda wywoływana przez listenera śmierci gracza
     * resetuje pozycje gracza i npc. cusuwa powerupy. 
     * jeśli zabrakło żyć, kończy gre uruchamiając zapis wyniku
     */
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
