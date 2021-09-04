package src;

public class SiteStatus {
    Site site;
    int siteId;
    int gold; // used in future leagues
    int maxMineSize; // used in future leagues
    StructureType structureType; // -1 = No structure, 2 = Barracks
    OwnerType owner; // -1 = No structure, 0 = Friendly, 1 = Enemy
    int param1;
    UnitType unitType;
    int towerRange;

    int threadArea = 0;
    int goalArea = 0;

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

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

    public int getTowerRange() {
        return towerRange;
    }

    public void setUnitTypeOrTowerRange(int unitType) {
        if (structureType.equals(StructureType.BARRACKS)) {
            this.unitType = UnitType.getType(unitType);
        } else if (structureType.equals(StructureType.TOWER)) {
            towerRange = unitType;
        }
    }

    public int getThreadArea() {
        return threadArea;
    }

    public int getGoalArea() {
        return goalArea;
    }

    public void calculateAreas() {
        threadArea = SiteThreadAreaCalculator.threadArea(getSite());
        goalArea = SiteGoalAreaCalculator.threadArea(getSite());
    }
}
