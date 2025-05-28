package visual;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import model.AgentModel;


/**
 * @class GameView
 * @brief Klasa obsługująca widok gry
 */
public class GameView extends BaseWindow {
    public interface GameListener {
        void onCloseGameWindow();
    }

    private GameListener listener;
    private JLabel scoreLabel;  
    private JLabel livesLabel; 
    private GameBoard level;
    private JPanel topPanel;
    private AgentModel model;
    private AnimatedTable table;

    public GameView() {

        super("Pacman!");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color (0,0,0));


        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(scoreLabel, BorderLayout.EAST);

        livesLabel = new JLabel("Lives: 0");
        livesLabel.setHorizontalAlignment(SwingConstants.LEFT);
        livesLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(livesLabel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (listener != null)
                    listener.onCloseGameWindow();
               // dispose();
            }
        });

        

    }

    public void setAgentModel(AgentModel model){
        this.model=model;
    }

    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void setLives(int lives) {
        livesLabel.setText("Lives: " + lives);
    }

    public void createLevel(int x, int y) {
        level = new GameBoard(y, x);
        initMap(level);
    }

    public GameBoard getBoard() {
        return level;
    }

    public void updateLevel(CellTypeVisu[][] board) {
        level.setBoard(board);
    }

    public void setKeyListener(KeyListener keyListener) {
        addKeyListener(keyListener);
        setFocusable(true);
        requestFocusInWindow();
    }

    public void setListener(GameListener l) {
        this.listener = l;
    }

    private void initMap(GameBoard level) {
        table = new AnimatedTable(level,model);

        JScrollPane sp = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);

        getContentPane().removeAll();
        add(topPanel, BorderLayout.NORTH);

        SpriteLevelRenderer renderer = new SpriteLevelRenderer();
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
       
        
        enableFullTableScaling(table, sp);
        add(sp, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        
        tableResize(28,28,table);

        setSize(table.getColumnCount() * (table.getColumnModel().getColumn(0).getPreferredWidth()),table.getRowCount() * table.getRowHeight());
    }

    private void tableResize(int cellW, int cellH, JTable table){
        table.setRowHeight(cellH);
        int cols = table.getColumnCount();
        for (int c = 0; c < cols; c++) {
            table.getColumnModel().getColumn(c).setPreferredWidth(cellW);
        }
        table.revalidate();
    }

    public AnimatedTable getAnimatedTable() {
        return this.table;  
    }

    private void enableFullTableScaling(JTable table, JScrollPane sp) {
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
                int cols = table.getColumnCount();
                int rows = table.getRowCount();
                if (cols == 0 || rows == 0)
                    return;
                int cellW = totalW / cols;
                if (cellW < 8)
                    cellW = 8;
                int cellH = totalH / rows;
                if (cellH < 8)
                    cellH = 8;

                
                table.setRowHeight(cellH);
                for (int column = 0; column < cols; column++) {
                    table.getColumnModel().getColumn(column).setPreferredWidth(cellW);
                }

                for (SpriteCellType.Type type : SpriteCellType.Type.values())
                    type.rescale(cellW, cellH);
            
                table.revalidate();
                table.repaint();;
            }
        });
    }



}
