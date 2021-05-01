public class UnitThreadAreaCalculator {

    public static int threadArea(Unit unit) {
        switch (unit.unitType) {
            case ARCHER:
                return 25;
            case KNIGHT:
                return unit.owner.equals(OwnerType.ENEMY) ? 20 + 100 * 3 : 20;
            case GIANT:
                return 40;
            case QUEEN:
                return 30;
        }

        throw new IllegalArgumentException("No matching thread area for unit type");
    }
}
