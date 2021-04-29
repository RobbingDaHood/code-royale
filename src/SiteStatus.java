public class SiteStatus {
    int siteId;
    int ignore1; // used in future leagues
    int ignore2; // used in future leagues
    StructureType structureType; // -1 = No structure, 2 = Barracks
    OwnerType owner; // -1 = No structure, 0 = Friendly, 1 = Enemy
    int turnsBeforeTraining;
    UnitType unitType;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getIgnore1() {
        return ignore1;
    }

    public void setIgnore1(int ignore1) {
        this.ignore1 = ignore1;
    }

    public int getIgnore2() {
        return ignore2;
    }

    public void setIgnore2(int ignore2) {
        this.ignore2 = ignore2;
    }

    public StructureType getStructureType() {
        return structureType;
    }

    public void setStructureType(int structureType) {
        this.structureType = StructureType.getType(structureType);
    }

    public void setStructureType(StructureType structureType) {
        this.structureType = structureType;
    }

    public OwnerType getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = OwnerType.getType(owner);
    }

    public void setOwner(OwnerType owner) {
        this.owner = owner;
    }

    public int getTurnsBeforeTraining() {
        return turnsBeforeTraining;
    }

    public void setTurnsBeforeTraining(int turnsBeforeTraining) {
        this.turnsBeforeTraining = turnsBeforeTraining;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(int unitType) {
        this.unitType = UnitType.getType(unitType);
    }

    public void setUnitType(UnitType setUnitType) {
        this.unitType = setUnitType;
    }
}
