package Models.Types;

public enum UnitType {
    QUEEN(-1),
    KNIGHT(0),
    ARCHER(1);

    public final int type;

    private UnitType(int type) {
        this.type = type;
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
