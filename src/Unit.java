import java.awt.*;

public class Unit implements HasPosition {
    Point position;
    OwnerType owner;
    UnitType unitType;
    int health;

    int threadArea = 0;

    public Unit(int x, int y, int owner, int unitType, int health) {
        this.position = new Point(x, y);
        this.owner = OwnerType.getType(owner);
        this.unitType = UnitType.getType(unitType);
        this.health = health;
        this.threadArea = UnitThreadAreaCalculator.threadArea(this);
    }

    public Point getPosition() {
        return position;
    }

    public OwnerType getOwner() {
        return owner;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public int getHealth() {
        return health;
    }
}
