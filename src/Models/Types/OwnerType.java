package Models.Types;

public enum OwnerType {
    NO_STRUCTURE(-1),
    FRIENDLY(0),
    ENEMY(1);

    public final int type;

    private OwnerType(int type) {
        this.type = type;
    }

    public static OwnerType getType(int type) {
        for (OwnerType e : values()) {
            if (e.type == type) {
                return e;
            }
        }
        return null;
    }
}
