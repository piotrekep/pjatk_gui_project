import javax.swing.*;
import javax.swing.text.NumberFormatter;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

import controller.GameController;
import controller.KeyHandler;
import visual.GameBoard;
import visual.GameView;
import model.GameLogic;
import controller.ExitKeyHandler;
import visual.BaseWindow;
import visual.DifficultyView;
import visual.LevelRenderer;
import visual.MenuView;
import visual.ScoreView;
import visual.myFrame;

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


