package visual;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class GameView extends JFrame {
    public interface GameListener {
        void onCloseGameWindow();
    }

    private GameListener listener;
    private JLabel scoreLabel;  
    private GameBoard level;

    public GameView() {

        super("Difficulty slector");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(scoreLabel, BorderLayout.NORTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (listener != null)
                    listener.onCloseGameWindow();
                dispose();
            }
        });

        setSize(800, 600);

    }

    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
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
        JTable table = new JTable(level);

        JScrollPane sp = new JScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);

        getContentPane().removeAll();
        add(scoreLabel, BorderLayout.NORTH);

        LevelRenderer renderer = new LevelRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        enableFullTableScaling(table, sp);
        add(sp, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);

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
