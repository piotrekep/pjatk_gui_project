package model;

/**
 * @enum PowerupType
 *       enum przechowuje rodzaje powerupów w raz z czasem ich trwania i
 *       wartością punktów
 */
public enum PowerupType {
    LIFE(0, 0),
    PEARL(100, 10),
    POINTS(200, 0),
    SPEED(0, 5),
    POOP(0, 10);

    private final int extraPoints;
    private final int duration;

    PowerupType(int extraPoints, int duration) {
        this.extraPoints = extraPoints;
        this.duration = duration;
    }

    public int getExtraPoints() {
        return extraPoints;
    }

    public int getDuration() {
        return duration;
    }

}
