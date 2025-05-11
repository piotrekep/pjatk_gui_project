package visual;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class ScoreView extends JFrame{
    public interface ScoreListener { void onCloseScoreWindow();}
    private ScoreListener listener;


    public ScoreView() {
        super("high scores");
        setSize(800, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

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


}
