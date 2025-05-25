package visual;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @class MenuView
 * @brief Główne menu aplikacji z trzema przyciskami: Nowa gra, Wyniki oraz Wyjście.
 *
 * Klasa dziedziczy po JFrame i wyświetla prosty interfejs menu.
 * Wykorzystuje listenera do obsługi kliknięć przycisków.
 */

public class MenuView extends JFrame{
        /**
     * Interfejs listenera menu.
     * Implementacje powinny definiować reakcje na kliknięcie przycisków.
     */
    public interface MenuListener {
        /** Wywoływane po kliknięciu "New Game". */
        void onNewGame();

        /** Wywoływane po kliknięciu "High Scores". */
        void onShowScores();

        /** Wywoływane po kliknięciu "Exit". */
        void onExit();
    }

    /** Obiekt implementujący MenuListener, który nasłuchuje zdarzeń z menu. */
    private MenuListener listener;

    /**
     * Konstruktor MenuView.
     *
     * Tworzy okno menu z trzema przyciskami i ustawia odpowiednie akcje.
     */

      public MenuView(){
        super("Menu");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        JPanel menuPanel =  new BackgroundJpanel(new GridLayout(3, 1, 10, 20),"images/menu_pacman.png");
        menuPanel.setBorder(BorderFactory.createEmptyBorder(80, 60, 80, 60));
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

       /**
     * Ustawia listenera zdarzeń menu.
     *
     * @param l obiekt implementujący MenuListener
     */
    public void setListener(MenuListener l) {
        this.listener = l;
    }
}
