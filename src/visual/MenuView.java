package visual;

import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.*;

/**
 * @class MenuView
 * @brief Główne menu aplikacji z trzema przyciskami: Nowa gra, Wyniki oraz Wyjście.
 *
 * Klasa dziedziczy po JFrame i wyświetla prosty interfejs menu.
 * Wykorzystuje listenera do obsługi kliknięć przycisków.
 */

public class MenuView extends BaseWindow{
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
     * @brief Konstruktor MenuView.
     *
     * Tworzy okno menu z trzema przyciskami i ustawia odpowiednie akcje.
     */

      public MenuView(){
        super("Menu");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        String img; 
        if(Math.random()<=0.15)
            img = "images/menu_pacan.png";
        else
            img = "images/menu_pacman.png";

        JPanel menuPanel =  new BackgroundJpanel(new GridLayout(3, 1, 10, 20),img);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(getHeight()/4, (int)(getWidth()/4.5), getHeight()/4, (int)(getWidth()/4.5)));
        add(menuPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {                
                 menuPanel.setBorder(BorderFactory.createEmptyBorder(getHeight()/4, (int)(getWidth()/4.5), getHeight()/4, (int)(getWidth()/4.5)));
            }
        });
        
        
        JButton newGameButton = new BackgroundJbutton("","images/NEW_GAME_button.png");
        JButton highScoreButton = new BackgroundJbutton("","images/HIGH_SCORES_button.png");
        JButton exitButton = new BackgroundJbutton("","images/EXIT_button.png");
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
     * @brief Ustawia listenera zdarzeń menu.
     *
     * @param l obiekt implementujący MenuListener
     */
    public void setListener(MenuListener l) {
        this.listener = l;
    }
}
