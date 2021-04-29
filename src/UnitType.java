public enum UnitType {
    QUEEN(-1, -1),
    KNIGHT(0, 80),
    ARCHER(1, 100),
    GIANT(2, 140);

    public final int type;
    public final int costToTrain;

    private UnitType(int type, int costToTrain) {
        this.type = type;
        this.costToTrain = costToTrain;
    }

    public static UnitType getType(int type) {
        for (UnitType e : values()) {
            if (e.type == type) {
                return e;
            }
        }
        return null;
    }
}
