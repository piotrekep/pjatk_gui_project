package model;

import java.awt.Point;
//import model.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import model.Agent.AgentListener;

/**
 * @class GameLogic
 * @brief Klasa implementuje logikę gry
 *
 *        Klasa implementuje logikę gry. i wszystkie stany jakie może przyjąć
 *        oblicza położenie gracza, kolizje, algorytm wybierania drogi itd.
 */

public class GameLogic implements AgentListener,
        AgentModel {
    /** tablica przechowująca ściany labiryntu */
    private CellType[][] labirynt;
    /** mapa odległości z każdego pola do grazcza */
    private int[][] distanceField;
    /** lista punktów spawnu przeciwników */
    private List<Point> npcSpawnPoints = new ArrayList<>();
    /** lista bytów na planszy */
    private final Map<Integer, Agent> agentList = new ConcurrentHashMap<>();
    /** ilość punktów do zdobyćia na planszy */
    private int maxPoints;
    /** wielkość planszy */
    private int x, y;
    /** aktualny level */
    public int level = 0;
    /** cooldown powerupów */
    private long powerupCooldown = System.nanoTime();
    /** start rozgrywki */
    public long gameStartTime;

    /** listener zdarzeń gry */
    public interface GameLogicListener {
        void onVictory();

        void onDeath(int lives);
    }

    private GameLogicListener listener;

    /**
     * Konstruktor klasy
     * 
     * @param x wielość plnaszy x
     * @param y wielkość planszy y
     */
    public GameLogic(int x, int y) {
        this.x = x;
        this.y = y;
        maxPoints = 0;
        generateLevel();
        gameStartTime = System.nanoTime();
        this.labirynt[3][3] = CellType.EMPTY;
        Player agent = SpawnPlayer(3, 3, 0);
        if (agent != null)
            agentList.put(0, agent);

        for (int i = 1; i <= npcSpawnPoints.size(); i++) {
            Npc npc = SpawnNpc(i, intToPersonality(((i - 1) % 5) + 1));
            if (npc != null) {

                agentList.put(i, npc);
            }
        }
    }

    /**
     * Metoda generująca labirynt na planszy,
     * oblicza liczbę spawn pointów dla danego rozmiaru,
     * i spawnóje wszystkie byty
     */
    public void generateLevel() {
        resetLevel();
        Labirynth lab = new Labirynth(x, y);
        lab.generate();

        this.labirynt = lab.labirynt;
        fillWithPoints(this.labirynt);

        for (int i = 0; i < labirynt.length; i++)
            for (int j = 0; j < labirynt[0].length; j++) {
                if (labirynt[i][j] == CellType.GHOSTFLOOR) {
                    npcSpawnPoints.add(new Point(i, j));
                }
            }
        Player player = (Player) agentList.get(0);
        if (player != null) {
            this.labirynt[3][3] = CellType.EMPTY;
            player.updateLevel(this.labirynt);
            player.setPosition(3, 3);
            player.stopPlayer();
            maxPoints--;
        }
        calcDistanceField();

        for (int i = 1; i <= npcSpawnPoints.size(); i++) {
            Npc npc = (Npc) agentList.get(i);
            if (npc != null) {
                npc.updateLevel(this.labirynt);
                reSpawnNpc(npc);
            }
        }
        level++;
    }

    /**
     * reset gry. wszyscy gracze zostają przesunięci na swoje spawny
     */
    public void resetLevel() {
        for (Agent agent : agentList.values()) {
            if (agent.id < 0)
                agentList.remove(agent.id);
            else {
                agent.moveToSpawn();
                if (agent instanceof Npc) {
                    ((Npc) agent).resetFreezeTimer();
                }
            }
        }

        powerupCooldown = System.nanoTime();
    }

    /**
     * aktualizacja stanu gracza
     * 
     * @param up    stan przycisku kierującego gracza w górę
     * @param down  stan przycisku kierującego gracza w dół
     * @param left  stan przycisku kierującego gracza w lewo
     * @param right stan przycisku kierującego gracza w prawo
     * @param speed prędkość poruszania się gracza
     */
    public void updatePlayer(boolean up, boolean down, boolean left, boolean right, double speed) {
        double speedMul = 1;
        Player player = (Player) agentList.get(0);
        if (up || down || left || right) {
            if (up)
                player.setDirection(1);
            if (down)
                player.setDirection(3);
            if (left)
                player.setDirection(4);
            if (right)
                player.setDirection(2);
        } else {

        }

        if (System.nanoTime() < player.pearlTime) {
            player.powered = true;
        } else {
            player.powered = false;
        }

        if (System.nanoTime() < player.speedTime) {
            speedMul = 1.5;
        } else {
            speedMul = 1;
        }

        player.move(speed * speedMul);

        if (System.nanoTime() < player.iceTime) {
            player.iced=true;
        } else {
            player.iced=false;
        }

        if (listener != null && player.getPoints() >= maxPoints)
            listener.onVictory();

    }

    /**
     * aktualizacja NPC
     * 
     * @param speed   prędkość poruszania się npc
     * @param id      identyfikator npc
     * @param powerup czy gracz jest pod wpływem powerupa. jeśli tak, zmienia
     *                osobowośc NPC
     */
    public void updateNpc(double speed, int id, boolean powerup) {
        Npc npc = (Npc) agentList.get(id);
        Player p = (Player) agentList.get(0);
        double speedMul = 1;
        double frozenSpeedMul = 1;
        if (npc != null) {
            if (distanceField != null){
                if (p.powered) {
                    npc.setPersonality(Personality.POWERUP);
                    speedMul = 0.75;
                } else {
                    npc.resetPersonality();
                    speedMul = 1;
                }
            if(p.iced){
                frozenSpeedMul = 0.25;
            }
            else{
                frozenSpeedMul = 1;
            }
            }
            npc.movePersonality(speed * speedMul * frozenSpeedMul, 10, distanceField);
        }
    }

    /**
     * Aktualizcja stanu powerupów. sprawdza kolizje z powerupem
     * 
     * @param id id powerupa
     */
    public void updatePowerup(int id) {
        if (id < 0) {
            Powerup powerup = (Powerup) agentList.get(id);
            if (powerup != null)
                if (distanceField != null)
                    powerup.checkCollision(distanceField);
        }
    }

    /**
     * aktualizacja wszystkich powerupów
     */
    public void updateAllPowerups() {
        for (int id : agentList.keySet()) {
            if (id < 0) {
                updatePowerup(id);
            }
        }
    }

    /**
     * aktualizacja wszystkich NPC
     * 
     * @param speed   prędkość NPC. wszystkie NPC poruszają się z tą samą prędkością
     * @param powerup czy gracz jest pod wpływem powerupa. zmienia osobowość NPC
     */
    public void updateAllNpcs(double speed, boolean powerup) {
        for (int i = 1; i <= npcSpawnPoints.size(); i++) {
            updateNpc(speed, i, powerup);
        }

    }

    /**
     * getter stanu gry. wykomentowany kod używany gdy nie używamy animacji
     */
    public CellType[][] getGameState() {

        CellType[][] gameState = new CellType[labirynt.length][];
        for (int i = 0; i < labirynt.length; i++) {
            gameState[i] = labirynt[i].clone();
        }
        // zmienianie tła tabeli, stary kod przed animacją
        for (Agent agent : agentList.values()) {
        switch (agent) {
        case Player p -> gameState[p.position.x][p.position.y] = CellType.PLAYER;
        case Npc n -> gameState[n.position.x][n.position.y] = n.getCellType();
        case Powerup pw -> {
            //gameState[pw.position.x][pw.position.y] = CellType.POWERUP;
            PowerupType powerType = ((Powerup) agent).getPowerup();
            gameState[pw.position.x][pw.position.y]  = CellType.valueOf("POWERUP_" + powerType.name());
        }
        default -> {
        }
        }
        }

        return gameState;

    }

    /**
     * spawn gracza
     * 
     * @param x  współrzędna x
     * @param y  współrzędna y
     * @param id id gracza. w tej implementacji gracz powinien mieć id=0
     * @return zwraca obiekt gracza
     */
    private Player SpawnPlayer(int x, int y, int id) {
        if (labirynt[x][y] == CellType.EMPTY) {
            Player player = new Player(x, y, id, labirynt);
            player.setLives(2);
            maxPoints--;
            return player;
        } else
            return null;
    }

    /**
     * spawn NPC
     * 
     * @param id          id npc. npc powinny mieć id>0
     * @param personality osobowość npc
     * @return
     */
    private Npc SpawnNpc(int id, Personality personality) {
        Random rand = new Random();
        Point spawn = npcSpawnPoints.get(rand.nextInt(npcSpawnPoints.size()));
        if (labirynt[spawn.x][spawn.y] != CellType.GHOSTFLOOR)
            return null;

        Npc npc = new Npc(spawn.x, spawn.y, id, labirynt, personality);
        npc.resetFreezeTimer();
        npc.setListener(this);
        return npc;
    }

    /**
     * respawnuje npc
     * 
     * @param npc npc którego chcemy respawnować
     * @return
     */
    private Npc reSpawnNpc(Npc npc) {
        Random rand = new Random();
        Point spawn = npcSpawnPoints.get(rand.nextInt(npcSpawnPoints.size()));
        if (labirynt[spawn.x][spawn.y] != CellType.GHOSTFLOOR)
            return null;
        npc.resetFreezeTimer();
        npc.setPosition(spawn.x, spawn.y);
        return npc;
    }

    /**
     * tworzenie, zrzucanie powerupa na ziemie
     * 
     * @param x     współrzedna x
     * @param y     współrzędna y
     * @param level tablica przechowująca stan planszy
     */
    private void createPowerup(int x, int y, CellType[][] level) {
        int id;
        int minKey = Collections.min(agentList.keySet());
        id = minKey - 1;
        Powerup p = new Powerup(x, y, id, level, getRandPowerup());
        agentList.put(id, p);
        p.setListener(this);
    }

    /**
     * losuj powerup
     * 
     * @return wylosowany powerup
     */
    private PowerupType getRandPowerup() {
        Random rand = new Random();
        double rng = rand.nextDouble(1);
        if (rng < 0.1)
            return PowerupType.LIFE;
        else if (rng < 0.4)
            return PowerupType.PEARL;
        else if (rng < 0.6)
            return PowerupType.SPEED;
        else if (rng < 0.8)
            return PowerupType.ICE;
        else
            return PowerupType.POINTS;
    }

    /**
     * wypełnienie planszy punktami
     * 
     * @param level plansza do wypełnienia
     */
    private void fillWithPoints(CellType[][] level) {
        for (int i = 0; i < level.length; i++)
            for (int j = 0; j < level[0].length; j++)
                if (level[i][j] == CellType.EMPTY) {
                    level[i][j] = CellType.POINT;
                    maxPoints++;
                }
    }

    /**
     * getter ilości punktów
     * 
     * @param id id gracza
     * @return
     */
    public int getPlayerScore(int id) {
        Player player = (Player) agentList.get(id);
        if (player != null) {
            return player.getTotalPoints();
        } else
            return 0;
    }

    /**
     * getter liczby wszystkich punktów na planszy. do obliczania wygranej
     */
    public int getLevelPoints() {
        return maxPoints;
    }

    public void setListener(GameLogicListener l) {
        this.listener = l;
    }

    /**
     * @deprecated
     *             rekurencyjny krok
     * @param x współrzędna X
     * @param y współrzędna Y
     * @param d odległość
     */
    private void step(int x, int y, int d) {
        // jeśli współrzędna po za zakresem, nie wykonumy dalej
        if (x < 0 || x >= this.x || y < 0 || y >= this.y)
            return;
        if (labirynt[x][y] == CellType.WALL || labirynt[x][y] == CellType.GHOSTHOUSE) {
            // jeśli pole na które nie można wejśc to ustawi -1 i przerwie
            distanceField[x][y] = -1;
            return;
        }
        // jeśli dystans dla pola już obliczony, to przerwij
        if (d >= distanceField[x][y])
            return;
        // ustaw dystans dla pola
        distanceField[x][y] = d;
        // wykonaj rekurencyjnie dla sąsiadujących pól
        step(x + 1, y, d + 1);
        step(x - 1, y, d + 1);
        step(x, y + 1, d + 1);
        step(x, y - 1, d + 1);
    }

    /**
     * @deprecated
     *             obliczanie pola odległości w sposób rekurencyjny
     *             działa bardzo wolno
     */
    public void calcDistanceFieldRec() {
        // pobiera obiekt gracza
        Player player = (Player) agentList.get(0);
        if (player == null)
            return;
        // inicjalizacja pola odległości równego wielkością labiryntu
        distanceField = new int[x][y];
        // wypełnia tablice maksymalnym dystansem
        for (int[] row : distanceField)
            Arrays.fill(row, Integer.MAX_VALUE);
        // wykonuje krok z pozycji w której znajduje się gracz przyjmując dystans 0
        step(player.position.x, player.position.y, 0);
        // przejście oczyszczające
        for (int x = 0; x < this.x; x++) {
            for (int y = 0; y < this.y; y++) {
                if (distanceField[x][y] == Integer.MAX_VALUE)
                    distanceField[x][y] = -1;
            }
        }
    }

    /**
     * Obliczanie pola dystansu przy użyciu algorytmu bfs
     */
    public void calcDistanceField() {
        try {
            // pobiera obiekt gracza (zawsze id 0 na liście)
            Player player = (Player) agentList.get(0);
            if (player == null)
                return;
            // inicjalizacja pola odległości równego wielkością labiryntu
            distanceField = new int[x][y];
            // wypełnienie pola maksymalnym dystansem
            for (int i = 0; i < x; i++)
                Arrays.fill(distanceField[i], Integer.MAX_VALUE);

            // kolejka dla współrzędnej x i y, tak długa jak aby zmieśćić całą tablice
            int[] qx = new int[x * y];
            int[] qy = new int[x * y];
            // head i tail dla bufora cyklicznego
            int head = 0, tail = 0;
            // ustawienie pozycji celu jakopozycji gracza
            int sx = player.position.x;
            int sy = player.position.y;
            // dystans zero w miejscu gdzie jest gracz
            distanceField[sx][sy] = 0;
            // zapisanie wartości startowej do kolejki
            qx[tail] = sx;
            qy[tail] = sy;
            // inkrementacja indeksu końca bufora
            tail++;

            // współrzędne pól sąsiadujących z aktualnym polem
            final int[] dx = { 1, -1, 0, 0 };
            final int[] dy = { 0, 0, 1, -1 };
            // tak długo jak głowa nie dogoniła ogona
            while (head < tail) {
                // pobierz współrzędne z końca bufora
                int cx = qx[head];
                int cy = qy[head];
                // inkrementacja głowy(początku bufora) po pobraniu
                head++;

                // pobierz wartość odległości z aktualnego pola i inkrementuj o jeden
                int nextDist = distanceField[cx][cy] + 1;
                // dla każdego pola sąsiadującego
                for (int dir = 0; dir < 4; dir++) {
                    // obliczamy współrzędne pola sąsiadującego
                    int nx = cx + dx[dir];
                    int ny = cy + dy[dir];
                    // jeśli pole wypada po za obszarem planszy skaczemy do następnej iteracji
                    if (nx < 0 || nx >= x || ny < 0 || ny >= y)
                        continue;
                    // jeżeli jest komórką do której nie można wejść skaczemy do następnej iteracji
                    if (labirynt[nx][ny] == CellType.WALL || labirynt[nx][ny] == CellType.GHOSTHOUSE)
                        continue;
                    // jeżeli odległość dla komórki została już obliczona skaczemy dalej
                    if (distanceField[nx][ny] <= nextDist)
                        continue;
                    // ustawiamy wartość pola jako wartość odległości
                    distanceField[nx][ny] = nextDist;
                    // dodajemy pole do kolejki
                    qx[tail] = nx;
                    qy[tail] = ny;
                    // inkrementujemy koniec kolejki
                    tail++;
                }
            }
            // przejście oczyszczające pola pomięte przez algorytm
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    if (distanceField[i][j] == Integer.MAX_VALUE)
                        distanceField[i][j] = -1;
                }
            }

        } catch (Exception e) {

            System.err.println("Błąd w calcDistanceField: " + e.getMessage());

            e.printStackTrace();
        }
    }

    /**
     * konwerter inta na osobowość
     * 
     * @param p int odpowiadający osobowości
     * @return osobowość odpowiadająca intowi
     */
    Personality intToPersonality(int p) {
        switch (p) {
            case 1:
                return Personality.CHASER;
            case 2:
                return Personality.AGGRO;
            case 3:
                return Personality.KEYBOARDWARRIOR;
            case 4:
                return Personality.HEADLESSCHICKEN;
            case 5:
                return Personality.COWARD;
            case 6:
                return Personality.POWERUP;
        }
        return Personality.CHASER;
    }

    /**
     * getter pola dystansu
     */
    public int[][] getDistanceField() {
        return distanceField;
    }

    /**
     * getter obiektu gracza
     * 
     * @param id id gracza
     * @return obiekt Player
     */
    public Player getPlayer(int id) {
        return (Player) agentList.get(id);
    }

    /**
     * obsługa kolizji z agentem
     */
    @Override
    public void onCollision(Agent source) {
        Player player = (Player) agentList.get(0);

        if (listener != null)
            if (source.id > 0)
                if (!player.powered)
                    listener.onDeath(level);
                else {
                    player.addBonusPoints(100);
                    ((Npc) source).resetFreezeTimer();
                    source.moveToSpawn();
                }
            else {
                Powerup powerup = (Powerup) source;
                player.setPowerup(powerup.getPowerup());

                switch (player.getPowerup()) {
                    case PowerupType.LIFE -> player.setLives(player.getLives() + 1);
                    case PowerupType.POINTS -> player.addBonusPoints(powerup.getPowerup().getExtraPoints());
                    case PowerupType.PEARL ->
                        player.pearlTime = System.nanoTime() + (player.getPowerup().getDuration() * 1_000_000_000l);
                    case PowerupType.SPEED ->
                        player.speedTime = System.nanoTime() + (player.getPowerup().getDuration() * 1_000_000_000l);
                     case PowerupType.ICE ->
                         player.iceTime = System.nanoTime() + (player.getPowerup().getDuration() * 1_000_000_000l);
                    default -> {
                    }
                }
                agentList.remove(powerup.id);

            }
    }

    /**
     * obsługa zrzucania powerupów
     */
    @Override
    public void onChangePosition(Agent source) {
        if (System.nanoTime() - powerupCooldown > 5_000_000_000l) {
            double rng = Math.random();
            if (rng < 0.25) {
                if (labirynt[source.position.x][source.position.y] != CellType.GHOSTFLOOR) {
                    powerupCooldown = System.nanoTime();
                    createPowerup(source.position.x, source.position.y, labirynt);
                }

            }
        }
    }

    /**
     * pobiera kopię listy agentów tylko do odczytu konieczną do animacji
     */
    @Override
    public Collection<Agent> getAgents() {
        return Collections.unmodifiableCollection(agentList.values());
    }

@Override
public Map<Point, Agent> getAgentsByLocation() {
    Map<Point, Agent> map = new HashMap<>();
    for (Agent agent : agentList.values()) {
        Point p = agent.position;
        map.put(p, agent);
    }
    return Collections.unmodifiableMap(map);
}

}
