package model;

public enum PowerupType {
    LIFE (0,0),
    PEARL (0,10),
    POINTS(100,0),
    SPEED (0,5);

    private final int   extraPoints;
    private final int   duration;

    PowerupType(int extraPoints, int duration) {
        this.extraPoints = extraPoints;
        this.duration    = duration;
    }

    
    public int getExtraPoints() {
        return extraPoints;
    }

    public int getDuration() {
        return duration;
    }


}
