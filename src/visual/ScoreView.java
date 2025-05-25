package visual;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class ScoreView extends JFrame{
    public interface ScoreListener { void onCloseScoreWindow();}
    private ScoreListener listener;
    private DefaultListModel<PlayerScore> model;
    private JList<PlayerScore> lista; 


    public ScoreView() {
        super("high scores");
        setSize(300, 400);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel highscorPanel = new BackgroundJpanel("images/pacman_staty.png");
        highscorPanel.setBorder(BorderFactory.createEmptyBorder(140, 66, 60, 66));
        add(highscorPanel);

        model = new DefaultListModel<>();
        
        lista = new JList<>(model);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setVisibleRowCount(10); 

       lista.setCellRenderer(new ListCellRenderer<PlayerScore>() {
            private final JPanel panel = new JPanel(new BorderLayout(5, 0));
            private final JLabel nameLabel = new JLabel();
            private final JLabel scoreLabel = new JLabel();

            {
                panel.setBorder(new EmptyBorder(2, 5, 2, 5));
                panel.add(nameLabel, BorderLayout.WEST);
                panel.add(scoreLabel, BorderLayout.EAST);
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends PlayerScore> list,
                                                          PlayerScore value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                nameLabel.setText(value.getName());
                scoreLabel.setText(String.valueOf(value.getScore()));

                if (isSelected) {
                    panel.setBackground(list.getSelectionBackground());
                    nameLabel.setForeground(list.getSelectionForeground());
                    scoreLabel.setForeground(list.getSelectionForeground());
                } else {
                    panel.setBackground(list.getBackground());
                    nameLabel.setForeground(list.getForeground());
                    scoreLabel.setForeground(list.getForeground());
                }
                return panel;
            }

        });

        JScrollPane scrollPane = new JScrollPane(lista);
        //getContentPane().setLayout(new BorderLayout());
        //getContentPane().add(scrollPane, BorderLayout.CENTER);
        highscorPanel.setLayout(new BorderLayout());
        highscorPanel.add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (listener != null) listener.onCloseScoreWindow();
                dispose();
            }
        });
    }

    public void setListener(ScoreListener l) {
        this.listener = l;
    }

    public void addHighScore(String name, int score){
        
        model.addElement(new PlayerScore(name, score));
        List<PlayerScore> temp = Collections.list(model.elements());
        Comparator<PlayerScore> cmp = Comparator.comparingInt(PlayerScore::getScore);
        cmp = cmp.reversed();
        temp.sort(cmp);
        model.clear();
        temp.forEach(model::addElement);

    }


}
