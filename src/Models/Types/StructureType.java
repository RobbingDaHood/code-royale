package Models.Types;

public enum StructureType {
    NO_STRUCTURE(-1),
    BARRACKS(2);

    public final int type;

    private StructureType(int type) {
        this.type = type;
    }

    public static StructureType getType(int type) {
        for (StructureType e : values()) {
            if (e.type == type) {
                return e;
            }
        }
        return null;
    }
}
