import javax.swing.*;

import controller.GameController;
import controller.KeyHandler;

import visual.GameView;
import controller.ExitKeyHandler;
import visual.DifficultyView;
import visual.MenuView;
import visual.ScoreView;

public class App {
    static MenuView menu;
    static ScoreView score;
    static DifficultyView difficulty;
    static GameView game;
    static GameController controller;

    public static void main(String[] args) {
        ExitKeyHandler.setupExitKeys(null);
        startApp();
    }

    private static void startApp() {
        ExitKeyHandler.setupExitKeys(() -> restart());
        initializeApp();
    }

    private static void initializeApp() {
        SwingUtilities.invokeLater(() -> {
            menu = new MenuView();
            score = new ScoreView();
            difficulty = new DifficultyView();
            game = new GameView();

            controller = new GameController(new KeyHandler(),
                    menu,
                    score,
                    difficulty,
                    game);
        });
    }

    private static void restart() {
        menu.dispose();
        score.dispose();
        difficulty.dispose();
        game.dispose();
        controller.stop();
        controller=null;
        initializeApp();
    }
}
