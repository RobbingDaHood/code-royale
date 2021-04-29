package Models;

import Models.Types.OwnerType;
import Models.Types.UnitType;

import java.awt.*;

public class Unit {
    Point position;
    OwnerType owner;
    UnitType unitType;
    int health;

    public Unit(int x, int y, int owner, int unitType, int health) {
        this.position = new Point(x, y);
        this.owner = OwnerType.getType(owner);
        this.unitType = UnitType.getType(unitType);
        this.health = health;
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
