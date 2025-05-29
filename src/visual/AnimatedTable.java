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

/**
 * @class AnimatedTable
 * @brief klasa rozszerzająca JTable na potrzeby animacji
 */
public class AnimatedTable extends JTable {
    /** lista agentów na potrzeby wizualzacji */
    private final AgentModel model;
    /** mapa spriteów */
    private final Map<SpriteCellType.Type, SpriteCellType.Type> spriteMap = new EnumMap<>(SpriteCellType.Type.class);
    /** wątek animacji */
    private Thread animationThread;
    /** running dla wątku */
    private volatile boolean running = false;

    /**
     * Konstruktor animowanej tabeli
     * 
     * @param tm    TableModel jak dla JTable
     * @param model Lista agentów
     */
    public AnimatedTable(TableModel tm, AgentModel model) {
        super(tm);
        this.model = model;

        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));

        setOpaque(true);
        setBackground(Color.BLACK);

        for (SpriteCellType.Type type : SpriteCellType.Type.values()) {
            spriteMap.put(type, type);
        }
    }

    /**
     * Metoda zajmująca się rysoiwaniem
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // oblicza szerokość komórek
        int cellW = getColumnModel().getColumn(0).getWidth();
        // oblicza wysokość komórek
        int cellH = getRowHeight();

        // dla każdego agenta
        for (Agent agent : model.getAgents()) {
            // oblicz współrzędne początkówe
            int sx = agent.getCol() * cellW;
            int sy = agent.getRow() * cellH;
            // oblicz współrzędne końcowe
            int tx = agent.getTargetCol() * cellW;
            int ty = agent.getTargetRow() * cellH;
            // pobierz postep ruchu
            double progress = agent.getMoveProgress();
            // interpolacja postępu ruchu do współrzędnych
            int dx = sx + (int) Math.round((tx - sx) * progress);
            int dy = sy + (int) Math.round((ty - sy) * progress);

            Image img;
            int height = cellH;
            int yOffset = 0;

            SpriteCellType.Type spriteType = null;
            // instrukcja pobierająca właściwy sprite dla agenta
            if (agent instanceof Player) {
                spriteType = SpriteCellType.Type.PLAYER;
                img = spriteType.getSprite(agent.getMoveProgress(), agent.getDirection());
            } else if (agent instanceof Npc) {
                Personality personality = ((Npc) agent).getPersonality();
                spriteType = SpriteCellType.Type.valueOf("NPC_" + personality.name());
                img = spriteType.getSprite(agent.getDirection());
                // animacja chodzenia. duchy kurczą się i powiększają w miare postępu ruchu
                height = (int) (cellH * changeSize(agent.getMoveProgress(), 0.9));
                yOffset = (height - cellH) / 2;
            } else if (agent instanceof Powerup) {
                PowerupType powerType = ((Powerup) agent).getPowerup();
                spriteType = SpriteCellType.Type.valueOf("POWERUP_" + powerType.name());
                img = spriteType.getSprite(0);
            } else {
                img = SpriteCellType.Type.EMPTY.getSprite(0);
            }
            // w przypadku braku sprite'a Magentowy kwadarat
            if (img == null) {
                img = createPlaceholder(Color.MAGENTA, cellW, cellH);
            }
            // rysowanie
            g2.drawImage(img,
                    dx, (dy) - yOffset,
                    cellW, height,
                    null);
        }
        // cleanup
        g2.dispose();
    }

    /**
     * Metoda tworząca placeholder
     * 
     * @param color kolor placeholdera
     * @param w     szerokość
     * @param h     wysokość
     * @return owal o podanych parametrach
     */
    private Image createPlaceholder(Color color, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(color);
        g2.fillOval(0, 0, w, h);
        g2.dispose();
        return img;
    }

    /**
     * skalowanie w miare postępu
     * 
     * @param progress postęp ruchu 0-1
     * @param minSize  minimalny rozmiar 0-1 w procentach
     * @return interpolacja rozmaru do postepu ruchu
     */
    private double changeSize(double progress, double minSize) {

        if (progress <= 0.5)
            return (minSize + (1 - minSize) * (progress / 0.5));
        else
            return (1 + (minSize - 1) * (progress / -0.5));

    }

    /**
     * Uruchamia wątek animacji (~60 FPS),
     * ywołuje repaint()
     */
    public synchronized void startAnimation() {
        if (running)
            return;
        running = true;

        animationThread = new Thread(() -> {
            final int fps = 100;
            final long frameTimeNs = 1_000_000_000L / fps;

            while (running) {
                long startNs = System.nanoTime();

                SwingUtilities.invokeLater(this::repaint);

                long elapsed = System.nanoTime() - startNs;
                long sleepNs = frameTimeNs - elapsed;
                if (sleepNs > 0) {
                    long ms = sleepNs / 1_000_000L;
                    int ns = (int) (sleepNs % 1_000_000L);
                    try {
                        Thread.sleep(ms, ns);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
        }, "AnimationThread");

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
