public class SiteStatus {
    int siteId;
    int gold; // used in future leagues
    int maxMineSize; // used in future leagues
    StructureType structureType; // -1 = No structure, 2 = Barracks
    OwnerType owner; // -1 = No structure, 0 = Friendly, 1 = Enemy
    int param1;
    UnitType unitType;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getMaxMineSize() {
        return maxMineSize;
    }

    public void setMaxMineSize(int maxMineSize) {
        this.maxMineSize = maxMineSize;
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

    public int getParam1() {
        return param1;
    }

    public void setParam1(int param1) {
        this.param1 = param1;
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
