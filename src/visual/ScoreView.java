package visual;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ScoreView extends JFrame{
    public interface ScoreListener { void onCloseScoreWindow();}
    private ScoreListener listener;


    public ScoreView() {
        super("high scores");
        setSize(800, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);


        String[] dane = { "Element 1", "Element 2", "Element 3", "Element 4", "Element 5" };
        
        
        JList<String> lista = new JList<>(dane);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setVisibleRowCount(3); 
        
       
        JScrollPane scrollPane = new JScrollPane(lista);
        
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        

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
