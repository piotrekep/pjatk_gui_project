package visual;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.text.NumberFormatter;


public class DifficultyView extends JFrame{
    public interface DifficultyListener { void onCloseDificultyWindow(); void onStartGame(int x, int y);}
    private DifficultyListener listener;

    
        public DifficultyView(){
        super("Difficulty slector");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (listener != null) listener.onCloseDificultyWindow();
                dispose();
            }
        });
        setSize(300, 300);
       
        JPanel difficultyPanel =  new JPanel(new GridLayout(3, 2, 10, 20));
        difficultyPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        add(difficultyPanel);
       

        JLabel labelY = new JLabel("Level size Y:");
        difficultyPanel.add(labelY); 
        JLabel labelX = new JLabel("Level size X:");
        difficultyPanel.add(labelX);


        NumberFormat intFormat = NumberFormat.getIntegerInstance();
        NumberFormatter nf = new NumberFormatter(intFormat);
        nf.setValueClass(Integer.class);                
        nf.setAllowsInvalid(false); 

        JFormattedTextField intFieldY = new JFormattedTextField(nf);
        intFieldY.setColumns(5);
        difficultyPanel.add(intFieldY);

        JFormattedTextField intFieldX = new JFormattedTextField(nf);
        intFieldX.setColumns(5);
        difficultyPanel.add(intFieldX);

        JButton starButton = new JButton("Start!");
        difficultyPanel.add(starButton);
        

        starButton.addActionListener(e -> {
            int sizeY;
            int sizeX;
            try{
                sizeY = Integer.parseInt(intFieldY.getText());              
                } catch (NumberFormatException ex) {
                     sizeY = 0;
                }
            try{
                sizeX = Integer.parseInt(intFieldX.getText());              
                } catch (NumberFormatException ex) {
                     sizeX = 0;
                }    
            if((sizeY>9 && sizeY <101) &&  (sizeX>9 && sizeX <101)) {

            
                listener.onStartGame(sizeX, sizeY);

               
            } 
            else 
                JOptionPane.showMessageDialog(this,"Level size must be between 10 and 100", "Error",JOptionPane.ERROR_MESSAGE);
        
        });
    
    }

    public void setListener(DifficultyListener l) {
        this.listener = l;
    }




}
