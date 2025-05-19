package visual;

import javax.swing.*;
import javax.swing.table.TableModel;

import model.Agent;
import model.AgentModel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AnimatedTable extends JTable {
    private final AgentModel model;
    private Thread animationThread;
    private volatile boolean running = false;

    /**
     * @param tm    dowolny TableModel (np. GameBoard)
     * @param model model, z którego pobierasz kolekcję Agentów
     */
    public AnimatedTable(TableModel tm, AgentModel model) {
        super(tm);
        this.model = model;
        setOpaque(true);
    }

    
    private Image createSprite() {
        BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.RED);
        g2.fillOval(0, 0, 50, 50);
        g2.dispose();
        return img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        int cellW = getColumnModel().getColumn(0).getWidth();
        int cellH = getRowHeight();

        for (Agent a : model.getAgents()) {
            int sx = a.getCol() * cellW;
            int sy = a.getRow() * cellH;
            int tx = a.getTargetCol() * cellW;
            int ty = a.getTargetRow() * cellH;

            double p = a.getMoveProgress();    
            int dx = sx + (int) Math.round((tx - sx) * p);
            int dy = sy + (int) Math.round((ty - sy) * p);

                
            Image img = createSprite();
            g2.drawImage(img,
                dx + 2, dy + 2,
                cellW - 4, cellH - 4,
                null);
        }

        g2.dispose();
    }



    /**
     * Uruchamia wątek animacji (~60 FPS),
     * on tylko wywołuje repaint() – logikę ruchu robią agenty same
     */
    public synchronized void startAnimation() {
        if (running) return;
        running = true;

        animationThread = new Thread(() -> {
            final int fps = 100;
            final long frameTimeNs = 1_000_000_000L / fps;

            while (running) {
                long startNs = System.nanoTime();

                // 1) repaint na EDT
                SwingUtilities.invokeLater(this::repaint);

                // 2) pilnuj dokładnego odstępu
                
                long elapsed = System.nanoTime() - startNs;
                long sleepNs = frameTimeNs - elapsed;
                if (sleepNs > 0) {
                    long ms = sleepNs / 1_000_000L;
                    int  ns = (int)(sleepNs % 1_000_000L);
                    try {
                      Thread.sleep(ms, ns);
                    } catch (InterruptedException ex) {
                        // jeśli ktoś przerwie wątek, kończymy
                        break;
                    }
                } 
            }
        }, "AnimationThread");

        animationThread.setDaemon(true);
        animationThread.start();
    }

    /** Zatrzymuje wątek animacji */
    public synchronized void stopAnimation() {
        running = false;
        if (animationThread != null) {
            animationThread.interrupt();
            animationThread = null;
        }
    }
}
