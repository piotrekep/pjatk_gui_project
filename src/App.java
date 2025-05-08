import javax.swing.*;
import javax.swing.text.NumberFormatter;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

import controller.GameController;
import controller.KeyHandler;
import visual.GameBoard;
import model.GameLogic;
import controller.ExitKeyHandler;
import visual.BaseWindow;
import visual.LevelRenderer;
import visual.VisuController;
import visual.myFrame;

public class App {
    private static JFrame menu, graFrame,scoresFrame,difSelectoFrame; 
    private static GameController controller;

    public static void main(String[] args) {
            ExitKeyHandler.setupExitKeys();

            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {}
 
            mainMenuHandler();
            scoresFrameHandler();
            gameFrameHandler();
            difficultyFrameHandler();
            

            menu.setVisible(true);
    }


    private static void backToMenu() {
        menu.setVisible(true);
    }

    private static void mainMenuHandler(){

        menu = new BaseWindow("Menu");
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu.setSize(300, 400);
        JPanel menuPanel =  new JPanel(new GridLayout(3, 1, 10, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        menu.add(menuPanel);

        JButton newGameButton = new JButton("New Game");
        JButton highScoreButton = new JButton("High Scores");
        JButton exitButton = new JButton("Exit");
        menuPanel.add(newGameButton);
        menuPanel.add(highScoreButton);
        menuPanel.add(exitButton);

        newGameButton.addActionListener(e -> {
            menu.setVisible(false);
            difSelectoFrame.setVisible(true);
        });

        highScoreButton.addActionListener(e -> {
            menu.setVisible(false);
            scoresFrame.setVisible(true);
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

    }

    private static void gameFrameHandler(){
        graFrame = new myFrame("nananana PACMAN!",App::backToMenu);
        graFrame.setSize(800, 600);

    }

    private static void scoresFrameHandler(){
        scoresFrame = new myFrame("high scores",App::backToMenu);
        scoresFrame.setSize(800, 600);
    }

    private static void difficultyFrameHandler(){
        difSelectoFrame = new myFrame("Difficulty slector",App::backToMenu);
        difSelectoFrame.setSize(300, 300);
       
        JPanel difficultyPanel =  new JPanel(new GridLayout(3, 2, 10, 20));
        difficultyPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        difSelectoFrame.add(difficultyPanel);
       

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
            if((sizeY>9 && sizeY <101) ||  (sizeX>9 && sizeX <101)) {
                
                difSelectoFrame.setVisible(false);
                graFrame.setVisible(true);
                
                controller = new GameController(new KeyHandler(),new GameLogic(sizeX,sizeY),new VisuController());
                initMap(controller.gamelogic.getBoard());

                //gameLogic = new GameLogic(level);
                graFrame.addKeyListener(controller.getKeyListener());
                graFrame.setFocusable(true);
                graFrame.requestFocusInWindow();

                Thread gameThread = new Thread(controller, "GameLoopThread");
                gameThread.start();  ;
            } 
            else 
                JOptionPane.showMessageDialog(difSelectoFrame,"Level size must be between 10 and 100", "Error",JOptionPane.ERROR_MESSAGE);
        
        });
    
    }

    private static void initMap(GameBoard level)
    {
        JTable table = new JTable(level);
        
        JScrollPane sp = new JScrollPane(
            table,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
         sp.setBorder(null);

        graFrame.getContentPane().removeAll();     

        LevelRenderer renderer = new LevelRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
             table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }      
        enableFullTableScaling(table,sp);
        graFrame.add(sp, BorderLayout.CENTER);
        graFrame.pack();                          
        graFrame.setLocationRelativeTo(null);  


    }


    private static void enableFullTableScaling(JTable table, JScrollPane sp) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setTableHeader(null);
        table.setShowGrid(false);
    
        JViewport vp = sp.getViewport();
        vp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = vp.getSize();
                int totalW = size.width;
                int totalH = size.height;
                int cols   = table.getColumnCount();
                int rows   = table.getRowCount();
                if (cols == 0 || rows == 0) return;
                  int cellW = totalW / cols;
                if(cellW<8) cellW=8;
                    int cellH = totalH / rows;
                if(cellH<8) cellH=8;

                table.setRowHeight(cellH);
                for (int c = 0; c < cols; c++) {
                    table.getColumnModel()
                         .getColumn(c)
                         .setPreferredWidth(cellW);
                }
                table.revalidate();
            }
        });
    }



}


