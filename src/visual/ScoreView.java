package visual;

import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * @class ScoreView
 * @brief obiekt widoku punktacji
 */

public class ScoreView extends BaseWindow {
    /** interface nasłuchujący sygnału zamykania okna */
    public interface ScoreListener {
        void onCloseScoreWindow();
    }

    /** obiekt listnera */
    private ScoreListener listener;
    /** lista wyników */
    private DefaultListModel<PlayerScore> model;
    /** obiekt listy */
    private JList<PlayerScore> lista;

    /** konstruktor okna */
    public ScoreView() {
        super("high scores");
        setSize(300, 400);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel highscorPanel = new BackgroundJpanel("images/pacman_staty.png");
        highscorPanel.setBorder(BorderFactory.createEmptyBorder((int) (getHeight() / 2.85), (int) (getWidth() / 5.5),
                (int) (getHeight() / 6.66), (int) (getWidth() / 5.5)));
        add(highscorPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                highscorPanel.setBorder(BorderFactory.createEmptyBorder((int) (getHeight() / 2.85),
                        (int) (getWidth() / 5.5), (int) (getHeight() / 6.66), (int) (getWidth() / 5.5)));
            }
        });

        model = new DefaultListModel<>();

        lista = new JList<>(model);
        lista.setVisibleRowCount(10);
        lista.setOpaque(false);
        lista.setBackground(new Color(0, 0, 0, 0));

        lista.setCellRenderer(new ListCellRenderer<PlayerScore>() {
            private final JPanel panel = new JPanel(new BorderLayout(5, 0));
            private final JLabel nameLabel = new JLabel();
            private final JLabel scoreLabel = new JLabel();

            {
                panel.setBorder(new EmptyBorder(2, 5, 2, 5));
                panel.add(nameLabel, BorderLayout.WEST);
                panel.add(scoreLabel, BorderLayout.EAST);
                nameLabel.setOpaque(false);
                scoreLabel.setOpaque(false);
                panel.setOpaque(false);
            }

            /**
             * @brief override wyglądu liosty
             * @param list         obiekt listy
             * @param value        wartość pola
             * @param index        pozycja
             * @param isSelected   czy wybrany
             * @param cellHasFocus focus
             * @return
             */
            @Override
            public Component getListCellRendererComponent(JList<? extends PlayerScore> list,
                    PlayerScore value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                Font labelFont = new Font("Arial", Font.BOLD, 22);
                nameLabel.setFont(labelFont);
                scoreLabel.setFont(labelFont);
                nameLabel.setForeground(new Color(255, 207, 38));
                scoreLabel.setForeground(new Color(255, 207, 38));

                nameLabel.setText(value.getName());
                scoreLabel.setText(String.valueOf(value.getScore()));
                panel.setOpaque(false);

                return panel;
            }

        });

        JScrollPane scrollPane = new MyScrollPane(lista);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        highscorPanel.setLayout(new BorderLayout());
        highscorPanel.add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (listener != null)
                    listener.onCloseScoreWindow();

            }
        });
    }

    public void setListener(ScoreListener l) {
        this.listener = l;
    }

    /**
     * @brief dodaje wynik do listy
     * @param name
     * @param score
     */
    public void addHighScore(String name, int score) {

        model.addElement(new PlayerScore(name, score));
        List<PlayerScore> temp = Collections.list(model.elements());
        Comparator<PlayerScore> cmp = Comparator.comparingInt(PlayerScore::getScore);
        cmp = cmp.reversed();
        temp.sort(cmp);
        model.clear();
        temp.forEach(model::addElement);

    }

}
