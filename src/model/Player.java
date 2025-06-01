package model;

/**
 * @class Player
 * @brief Definicja Gracza w grze
 *
 *        Klasa rozszerza klasę Agent o dodatkową funkcjonalność odróżniającą
 *        byt gracza od innych.
 */

public class Player extends Agent {
    /**
     * punkty zdobyte przez gracza.
     * Są to punkty "zjedzone" na podstawie których sprawdzamy czy poziom został
     * ukończony
     */
    private int points = 0;
    /** dodatkowe punkty zdobytye przez powerupy */
    private int bonusPoints = 0;
    /** liczba żyć */
    private int lives = 0;
    /** czy gracz jest po zjedzeniu powerupa(perły) */
    public boolean powered = false;
    /** czas do końca perły */
    public long pearlTime = 0;
    /** czas do końca przyspieszenia */
    public long speedTime = 0;
    /** czas do końca zamrożenia */
    public long iceTime = 0;
    /** zamrozony */
    public boolean iced = false;
    /** rodzaj zebranego powerupa */
    private PowerupType collected;

    /**
     * konstruktor gracza.
     *
     * @param x     współrzędna wiersza startowego
     * @param y     współrzędna kolumny startowej
     * @param id    unikalny identyfikator agenta
     * @param level mapa gry
     */

    public Player(int x, int y, int id, CellType[][] level) {
        super(x, y, id, level);

    }

    /**
     * Override ruchu. Rozbudowany o zbieranie punktów
     *
     * @param speed prędkość poruszania się w kratkach na sekundę
     * @return true jeśli wykonano ruch, false w przeciwnym razie
     */
    @Override
    public boolean move(double speed) {

        if (super.move(speed)) {
            checkForPoint();
            return true;
        } else
            return false;
    }

    /**
     * Override ruchu. Rozbudowany o zbieranie punktów
     *
     * @return true jeśli wykonano ruch, false w przeciwnym razie
     */
    @Override
    public void move() {
        super.move();
        checkForPoint();
    }

    /**
     * sprawdza czy gracz zjadł punkt.
     * Jeżeli gracz znajduje się na polu z punktem, pole jest resetowane, a punkt
     * dodawany
     */
    private void checkForPoint() {
        if (level[position.x][position.y] == CellType.POINT) {
            points++;
            level[position.x][position.y] = CellType.EMPTY;
        }

    }

    /**
     * Zatrzymuje ruch gracza
     */
    public void stopPlayer() {
        newDirection = 0;
        direction = 0;
    }

    /**
     * pobiera ilość punktów
     */
    public int getPoints() {
        return points;
    }

    /** pobiera całkowitą ilość punktów */
    public int getTotalPoints() {
        return points + bonusPoints;
    }

    /**
     * dodaje bonusowe punkty
     * 
     * @param points ilość punktów do dodania
     */
    public void addBonusPoints(int points) {
        this.bonusPoints += points;
    }

    /**
     * ustawia ilość żyć
     * 
     * @param lives nowa ilość żyć
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * pobiera ilość żyć
     * 
     * @return ilość żyć
     */
    public int getLives() {
        return this.lives;
    }

    /**
     * pobiera rodzaj powerupa zebranego przez gracza
     * 
     * @return rodzaj powerupa
     */
    public PowerupType getPowerup() {
        return collected;
    }

    /**
     * ustawia powerup gracza
     * 
     * @param pwr powerup
     */
    public void setPowerup(PowerupType pwr) {
        this.collected = pwr;
    }

    /**
     * zmiana implementacji moveUP zabraniająca graczowi wchodzenia do domu duchów
     */

    @Override
    public void moveUp() {
        if (level[position.x - 1][position.y] != CellType.WALL &&
                level[position.x - 1][position.y] != CellType.GHOSTHOUSE &&
                level[position.x - 1][position.y] != CellType.GHOSTFLOOR)

            position.x--;
    }

    /**
     * zmiana implementacji moveDown zabraniająca graczowi wchodzenia do domu duchów
     */
    @Override
    public void moveDown() {
        if (level[position.x + 1][position.y] != CellType.WALL &&
                level[position.x + 1][position.y] != CellType.GHOSTHOUSE &&
                level[position.x + 1][position.y] != CellType.GHOSTFLOOR)

            position.x++;
    }

    /**
     * zmiana implementacji moveLeft zabraniająca graczowi wchodzenia do domu duchów
     */
    @Override
    public void moveLeft() {
        if (level[position.x][position.y - 1] != CellType.WALL &&
                level[position.x][position.y - 1] != CellType.GHOSTHOUSE &&
                level[position.x][position.y - 1] != CellType.GHOSTFLOOR)

            position.y--;
    }

    /**
     * zmiana implementacji moveRight zabraniająca graczowi wchodzenia do domu
     * duchów
     */
    @Override
    public void moveRight() {
        if (level[position.x][position.y + 1] != CellType.WALL &&
                level[position.x][position.y + 1] != CellType.GHOSTHOUSE &&
                level[position.x][position.y + 1] != CellType.GHOSTFLOOR)

            position.y++;
    }

}
