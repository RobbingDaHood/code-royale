package src;

public class SiteThreadAreaCalculator {

    public static int threadArea(Site site) {
        switch (site.getSiteStatus().getStructureType()) {
            case BARRACKS:
                return site.getSiteStatus().getOwner().equals(OwnerType.ENEMY) &&
                        site.getSiteStatus().getParam1() != 0  && //Training
                        site.getSiteStatus().getUnitType().equals(UnitType.KNIGHT)
                        ? site.getRadius() + 100 : site.getRadius();
            case TOWER:
                return site.getSiteStatus().getOwner().equals(OwnerType.ENEMY) ?
                        site.getSiteStatus().getTowerRange() : site.getRadius();
            case MINE:
            case NO_STRUCTURE:
                return site.getRadius();
        }

        throw new IllegalArgumentException("No matching src.SiteThreadAreaCalculator for unit type");
    }
}
