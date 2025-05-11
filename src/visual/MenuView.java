package visual;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MenuView extends JFrame{
    public interface MenuListener { void onNewGame(); void onShowScores(); void onExit(); }
    private MenuListener listener;

      public MenuView(){
        super("Menu");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        JPanel menuPanel =  new JPanel(new GridLayout(3, 1, 10, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        add(menuPanel);

        JButton newGameButton = new JButton("New Game");
        JButton highScoreButton = new JButton("High Scores");
        JButton exitButton = new JButton("Exit");
        menuPanel.add(newGameButton);
        menuPanel.add(highScoreButton);
        menuPanel.add(exitButton);

        newGameButton.addActionListener(e -> {
            listener.onNewGame();
        });

        highScoreButton.addActionListener(e -> {
            listener.onShowScores();
        });

        exitButton.addActionListener(e -> {
            listener.onExit();
        });

    }

    public void setListener(MenuListener l) {
        this.listener = l;
    }
}
