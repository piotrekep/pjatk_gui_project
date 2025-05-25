package visual;

import java.awt.GridLayout;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

public class DifficultyView extends JFrame {
    public interface DifficultyListener {
        void onCloseDificultyWindow();

        void onStartGame(int x, int y);
    }

    private DifficultyListener listener;

    public DifficultyView() {
        super("Difficulty slector");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (listener != null)
                    listener.onCloseDificultyWindow();
                dispose();
            }
        });
        setSize(300, 400);

        JPanel difficultyPanel = new BackgroundJpanel(new GridLayout(2, 2, 10, 20),"images/pacman_rozmiar.png");
        difficultyPanel.setBorder(BorderFactory.createEmptyBorder((int)(getHeight()/2.2), (int)(getWidth()/7.5), getHeight()/4, (int)(getWidth()/7.5)));
        add(difficultyPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {                
                difficultyPanel.setBorder(BorderFactory.createEmptyBorder((int)(getHeight()/2.2), (int)(getWidth()/7.5), getHeight()/4, (int)(getWidth()/7.5)));
            }
        });


        //JLabel labelY = new JLabel("Level size Y:");
        //difficultyPanel.add(labelY);
        //JLabel labelX = new JLabel("Level size X:");
        //difficultyPanel.add(labelX);

        NumberFormat intFormat = NumberFormat.getIntegerInstance();
        NumberFormatter nf = new NumberFormatter(intFormat) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text == null || text.trim().isEmpty()) {
                    return 0;
                }
                return super.stringToValue(text);
            }
        };

        nf.setValueClass(Integer.class);
        nf.setAllowsInvalid(false);

        JFormattedTextField intFieldY = new JFormattedTextField(nf);
        intFieldY.setColumns(5);
        difficultyPanel.add(intFieldY);

        JFormattedTextField intFieldX = new JFormattedTextField(nf);
        intFieldX.setColumns(5);
        difficultyPanel.add(intFieldX);

        JButton starButton = new BackgroundJbutton("","images/START_button.png");
        difficultyPanel.add(starButton);

        starButton.addActionListener(e -> {
            int sizeY;
            int sizeX;
            try {
                sizeY = Integer.parseInt(intFieldY.getText());
            } catch (NumberFormatException ex) {
                sizeY = 0;
            }
            try {
                sizeX = Integer.parseInt(intFieldX.getText());
            } catch (NumberFormatException ex) {
                sizeX = 0;
            }
            if ((sizeY > 9 && sizeY < 101) && (sizeX > 9 && sizeX < 101)) {

                listener.onStartGame(sizeX, sizeY);

            } else
                JOptionPane.showMessageDialog(this, "Level size must be between 10 and 100", "Error",
                        JOptionPane.ERROR_MESSAGE);

        });

    }

    public void setListener(DifficultyListener l) {
        this.listener = l;
    }

}
