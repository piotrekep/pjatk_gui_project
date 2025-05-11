import javax.swing.*;

import controller.GameController;
import controller.KeyHandler;

import visual.GameView;

import controller.ExitKeyHandler;

import visual.DifficultyView;

import visual.MenuView;
import visual.ScoreView;


public class App {
    

    public static void main(String[] args) {
            ExitKeyHandler.setupExitKeys();

            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {}
 
            SwingUtilities.invokeLater(() -> {
                MenuView menu = new MenuView();
                ScoreView score = new ScoreView();
                DifficultyView difficulty = new DifficultyView();
                GameView game = new GameView();

                GameController controller = new GameController(new KeyHandler(),
                                                                menu,
                                                                score,
                                                                difficulty,
                                                                game); 


            });
            
            
 
            
    }





}


