package src;

public class SiteGoalAreaCalculator {

    public static int threadArea(Site site) {
        switch (site.getSiteStatus().getStructureType()) {
            case MINE:
            case BARRACKS:
                return site.getSiteStatus().getOwner().equals(OwnerType.ENEMY)
                        ? site.getRadius() + 200 : 0;
            case TOWER:
                return 0;
            case NO_STRUCTURE:
                return site.getSiteStatus().getOwner().equals(OwnerType.ENEMY)
                        ? site.getRadius() + 500 : 0;
        }

        throw new IllegalArgumentException("No matching src.SiteGoalAreaCalculator for unit type");
    }
}
