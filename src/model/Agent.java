package model;

import java.awt.Point;

/**
 * @class Agent
 * @brief Definicja "bytu" w grze
 *
 *        Klasa definiuje "byt" w grze. Bytami są gracze, NPC, i Powerupy.
 *        Agent reprezentuje istotę w grze, która może się poruszać po planszy,
 *        zmieniać kierunki i reagować na kolizje.
 *        Wykorzystuje listenera do obsługi zdarzeń.
 */

abstract public class Agent {
    /**
     * Interfejs listenera Agenta.
     * Implementacje powinny definiować reakcje na zdarzenia.
     */
    public interface AgentListener {
        /** Wywoływane po kolizji */
        void onCollision(Agent source);

        /** Wywoływane przy zmianie położenia */
        void onChangePosition(Agent source);
    }

    /** Obiekt implementujący AgentListener */
    protected AgentListener listener;

    /** Aktualna pozycja agenta. */
    public Point position;
    /** Czy agent został zrespawnowany. */
    public boolean spawned = false;
    /** id agenta */
    public int id;
    /** Plansza po której porusza się Agent */
    protected CellType[][] level;
    /** czas czas wywołania metody move(double speed) */
    private long lastTime;
    /** kierunek w którym porusza się agent */
    protected int direction = 0;
    /** zakolejkowana zmiana kierunku */
    protected int newDirection = 0;
    /** Współrzędne punktu spawnu */
    private Point spawPoint;
    /** współrzędne punktu docelowego */
    private Point target;
    /** postęp ruchu. obliczany na podstawie wartości lastTime i prędkości */
    protected double moveProgress = 1.0;
    /** prędkość poruszania się Agenta */
    private double speed = 0.0;
    

    /**
     * konstruktor agenta.
     *
     * @param x     współrzędna wiersza startowego
     * @param y     współrzędna kolumny startowej
     * @param id    unikalny identyfikator agenta
     * @param level mapa gry
     */

    Agent(int x, int y, int id, CellType[][] level) {
        this.position = new Point(x, y);
        this.spawPoint = new Point(x, y);
        this.target = new Point(x, y);
        this.lastTime = System.nanoTime();

        this.id = id;
        this.level = level;
    }

    /**
     * Ustawia plansze
     * 
     * @param level
     */
    public void updateLevel(CellType[][] level) {
        this.level = level;
    }

    /**
     * Ustawia listenera zdarzeń agenta.
     * 
     * @param l obiekt implementujący AgentListener
     */

    public void setListener(AgentListener l) {
        this.listener = l;
    }

    /**
     * Przemieszcza agenta w czasie na podstawie prędkości.
     *
     * @param speed prędkość poruszania się w kratkach na sekundę
     * @return true jeśli wykonano ruch, false w przeciwnym razie
     */

    public boolean move(double speed) {
        this.speed = speed; 
        long now = System.nanoTime();
        moveProgress = (now - lastTime) / 1_000_000_000.0;
       
    //    if (moveProgress < 0.1)
    //         changeDirection();

        if (speed != 0) {
            switch (direction) {
                case 1: {
                    target.x = position.x - 1;
                    target.y = position.y;
                    break;
                }
                case 2: {
                    target.y = position.y + 1;
                    target.x = position.x;
                    break;
                }
                case 3: {
                    target.x = position.x + 1;
                    target.y = position.y;
                    break;
                }
                case 4: {
                    target.y = position.y - 1;
                    target.x = position.x;
                    break;
                }
                default: {
                    break;
                }
            }
            if (level[target.x][target.y] == CellType.WALL || level[target.x][target.y] == CellType.GHOSTHOUSE) {
                target.x = position.x;
                target.y = position.y;
            }

            if (moveProgress > 1.0 / speed) {
               
                switch (direction) {
                    case 1 -> moveUp();
                    case 2 -> moveRight();
                    case 3 -> moveDown();
                    case 4 -> moveLeft();
                    default -> {
                    }
                }
                
                if (listener != null)
                    listener.onChangePosition(this);
                changeDirection();
                lastTime = now;
                return true;
            }
        }
        return false;
    }

    /**
     * rcuh bez prędkości, aktualzacja w każdym wywołaniu
     */
    public void move() {

        changeDirection();
        switch (direction) {
            case 1 -> moveUp();
            case 2 -> moveRight();
            case 3 -> moveDown();
            case 4 -> moveLeft();
            default -> {
            }
        }
    }

    /**
     * Ustawia nowy kierunek ruchu.
     * 
     * @param direction kierunek (1-góra, 2-prawo, 3-dół, 4-lewo)
     */
    public void setDirection(int direction) {
        switch (direction) {
            case 1 -> this.newDirection = 1;
            case 2 -> this.newDirection = 2;
            case 3 -> this.newDirection = 3;
            case 4 -> this.newDirection = 4;
            default -> {
                this.newDirection = 0;
            }
        }
    }

    /**
     * Ustawia pozycję agenta na mapie.
     * 
     * @param x współrzędna wiersza
     * @param y współrzędna kolumny
     */
    public void setPosition(int x, int y) {
        this.position.x = x;
        this.position.y = y;
        this.target.x = x;
        this.target.y = y;

    }

    /**
     * pobiera aktualny kierunek
     * 
     * @return aktualny kierunek w którym porusza się agent (1-góra, 2-prawo, 3-dół,
     *         4-lewo)
     */
    public int getDirection() {
        return direction;
    }

    /**
     * reset agenta. metoda przenosi agenta na jego punkt startowy
     */
    public void moveToSpawn() {
        this.position.x = this.spawPoint.x;
        this.position.y = this.spawPoint.y;
        this.target.x = this.spawPoint.x;
        this.target.y = this.spawPoint.y;
        direction = 0;
        newDirection = 0;
    }

    /**
     * zmiana kierunku. sprawdza czy zmiana jest możliwa, jeśli tak, to zmienia
     */
    protected void changeDirection() {
        switch (newDirection) {
            case 1 -> {
                if (moveUpPossible())
                    direction = newDirection;
            }
            case 2 -> {
                if (moveRightPossible())
                    direction = newDirection;
            }
            case 3 -> {
                if (moveDownPossible())
                    direction = newDirection;
            }
            case 4 -> {
                if (moveLeftPossible())
                    direction = newDirection;
            }
            default -> {
            }
        }
    }

    /**
     * pobiera aktualną kolumne w której jest agent
     * 
     * @return numer kolumny
     */
    public int getCol() {
        return position.y;
    }

    /**
     * pobiera aktualny wiersz w którym jest agent
     * 
     * @return numer wiersza
     */
    public int getRow() {
        return position.x;
    }

    /**
     * pobiera kolumne w kierunku której porsza się agent
     * 
     * @return numer kolumny
     */
    public int getTargetCol() {
        return target.y;
    }

    /**
     * pobiera wiersz w kierunku którego porsza się agent
     * 
     * @return numer wiersza
     */
    public int getTargetRow() {
        return target.x;
    }

    /**
     * pobiera postęp ruchu agenta
     * 
     * @return postęp ruchu 0.0-1.0
     */

    public double getMoveProgress() {
        if(position.x != target.x || position.y !=target.y)
            return Math.min(1.0, Math.max(0.0, moveProgress / (1.0 / this.speed)));
        else
            return 0;
    }

    /**
     * ruch agenta w górę
     */

    public void moveUp() {
        if (level[position.x - 1][position.y] != CellType.WALL &&
                level[position.x - 1][position.y] != CellType.GHOSTHOUSE)
            position.x--;
    }

    /**
     * ruch agenta w dół
     */

    public void moveDown() {
        if (level[position.x + 1][position.y] != CellType.WALL &&
                level[position.x + 1][position.y] != CellType.GHOSTHOUSE)
            position.x++;
    }

    /**
     * ruch agenta w lewo
     */
    public void moveLeft() {
        if (level[position.x][position.y - 1] != CellType.WALL &&
                level[position.x][position.y - 1] != CellType.GHOSTHOUSE)
            position.y--;
    }

    /**
     * ruch agenta w prawo
     */
    public void moveRight() {
        if (level[position.x][position.y + 1] != CellType.WALL &&
                level[position.x][position.y + 1] != CellType.GHOSTHOUSE)
            position.y++;
    }

    /**
     * sprawdza czy ruch w górę jest możliwy
     * 
     * @return true jeśli jest false jeśli nie
     */
    public boolean moveUpPossible() {
        if (level[position.x - 1][position.y] != CellType.WALL &&
                level[position.x - 1][position.y] != CellType.GHOSTHOUSE)
            return true;
        else
            return false;
    }

    /**
     * sprawdza czy ruch w dół jest możliwy
     * 
     * @return true jeśli jest false jeśli nie
     */
    public boolean moveDownPossible() {
        if (level[position.x + 1][position.y] != CellType.WALL &&
                level[position.x + 1][position.y] != CellType.GHOSTHOUSE)
            return true;
        else
            return false;
    }

    /**
     * sprawdza czy ruch w lewo jest możliwy
     * 
     * @return true jeśli jest false jeśli nie
     */

    public boolean moveLeftPossible() {
        if (level[position.x][position.y - 1] != CellType.WALL &&
                level[position.x][position.y - 1] != CellType.GHOSTHOUSE)
            return true;
        else
            return false;
    }

    /**
     * sprawdza czy ruch w prawo jest możliwy
     * 
     * @return true jeśli jest false jeśli nie
     */
    public boolean moveRightPossible() {
        if (level[position.x][position.y + 1] != CellType.WALL &&
                level[position.x][position.y + 1] != CellType.GHOSTHOUSE)
            return true;
        else
            return false;
    }

    /**
     * sprawdza czy ruch w zadanym kierunku jest możliwy
     * 
     * @return true jeśli jest false jeśli nie
     */
    public boolean moveInDirPossible(int direction) {
        switch (direction) {
            case 1:
                return moveUpPossible();
            case 2:
                return moveRightPossible();
            case 3:
                return moveDownPossible();
            case 4:
                return moveLeftPossible();
            default:
                return false;
        }
    }
}
