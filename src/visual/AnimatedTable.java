package visual;

import javax.swing.*;
import javax.swing.table.TableModel;

import model.Agent;
import model.AgentModel;
import model.Personality;
import model.Player;
import model.Npc;
import model.Powerup;
import model.PowerupType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

public class AnimatedTable extends JTable {
    private final AgentModel model;
    private final Map<SpriteCellType.Type, Image> spriteMap = new EnumMap<>(SpriteCellType.Type.class);
    private Thread animationThread;
    private volatile boolean running = false;


    public AnimatedTable(TableModel tm, AgentModel model) {
        super(tm);
        this.model = model;

        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
   
        setOpaque(false);

        for (SpriteCellType.Type type : SpriteCellType.Type.values()) {
            spriteMap.put(type, type.getSprite());
        }
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
            int dir;
            Image img;
            if (a instanceof Player) {
                img = spriteMap.get(SpriteCellType.Type.PLAYER);
            } else if (a instanceof Npc) {
                Personality pers = ((Npc) a).getPersonality();
                SpriteCellType.Type key = SpriteCellType.Type.valueOf("NPC_" + pers.name());
                img = spriteMap.get(key);
            } else if (a instanceof Powerup) {
                PowerupType pt = ((Powerup) a).getPowerup();
                SpriteCellType.Type key = SpriteCellType.Type.valueOf("POWERUP_" + pt.name());
                img = spriteMap.get(key);
            } else {
                img = spriteMap.get(SpriteCellType.Type.EMPTY);
            }

            if (img == null) {
                // placeholder
                img = createPlaceholder(Color.MAGENTA, cellW, cellH);
            }

              
            // g2.translate(dx + (cellW - 4)/2, dy + (cellH - 4)/2);
            // a.getDir();
            // g2.rotate(angle);
            // g2.translate(dx + (cellW - 4)/2, dy + (cellH - 4)/2); 

               g2.drawImage(img,
                dx + 2, dy + 2,
                cellW - 4, cellH - 4,
                null);
        }
        g2.dispose();
    }

    private Image createPlaceholder(Color color, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(color);
        g2.fillOval(0, 0, w, h);
        g2.dispose();
        return img;
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
