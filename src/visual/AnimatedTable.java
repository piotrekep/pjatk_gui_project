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
    //private final Map<SpriteCellType.Type, Image> spriteMap = new EnumMap<>(SpriteCellType.Type.class);
    private final Map<SpriteCellType.Type, SpriteCellType.Type> spriteMap = new EnumMap<>(SpriteCellType.Type.class);
    private Thread animationThread;
    private volatile boolean running = false;


    public AnimatedTable(TableModel tm, AgentModel model) {
        super(tm);
        this.model = model;

        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
   
        setOpaque(false);

        for (SpriteCellType.Type type : SpriteCellType.Type.values()) {
            spriteMap.put(type, type);
        }
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int cellW = getColumnModel().getColumn(0).getWidth();
        int cellH = getRowHeight();
        

        for (Agent agent : model.getAgents()) {
            int sx = agent.getCol() * cellW;
            int sy = agent.getRow() * cellH;
            int tx = agent.getTargetCol() * cellW;
            int ty = agent.getTargetRow() * cellH;

            double progress = agent.getMoveProgress();
            int dx = sx + (int) Math.round((tx - sx) * progress);
            int dy = sy + (int) Math.round((ty - sy) * progress);
            int direction;
            Image img;
            int height=cellH;
            int yOffset=0;
            
            SpriteCellType.Type spriteType = null;

            if (agent instanceof Player) {
                spriteType = SpriteCellType.Type.PLAYER;
                img = spriteType.getSprite(0);
            } else if (agent instanceof Npc) {
                Personality personality = ((Npc) agent).getPersonality();
                spriteType = SpriteCellType.Type.valueOf("NPC_" + personality.name());
                img = spriteType.getSprite(agent.getDirection());

                height =(int)(cellH * changeSize(agent.getMoveProgress(),0.9));
                yOffset=(height-cellH)/2;
            } else if (agent instanceof Powerup) {
                PowerupType powerType = ((Powerup) agent).getPowerup();
                spriteType = SpriteCellType.Type.valueOf("POWERUP_" + powerType.name());
                img = spriteType.getSprite(0);
            } else {
                img = SpriteCellType.Type.EMPTY.getSprite(0);
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
                dx + 2, (dy + 2)-yOffset,
                cellW - 4, height- 4,
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


    private double changeSize(double progress, double minSize){

        if(progress<=0.5)
            return (minSize + (1 - minSize) * (progress / 0.5));
        else
            return  (1 + (minSize - 1) * (progress / -0.5));
        
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

                long elapsed = System.nanoTime() - startNs;
                long sleepNs = frameTimeNs - elapsed;
                if (sleepNs > 0) {
                    long ms = sleepNs / 1_000_000L;
                    int  ns = (int)(sleepNs % 1_000_000L);
                    try {
                      Thread.sleep(ms, ns);
                    } catch (InterruptedException ex) {
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
