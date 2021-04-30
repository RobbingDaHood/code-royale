import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private static int gameTurns = 0;
    private static int numUnits = 0;
    private static Unit ourQueen = null;
    private static Unit theirQueen = null;
    private static List<Unit> units = new LinkedList<>();
    private static List<Unit> enemyKnights = new LinkedList<>();
    private static List<Unit> myGiants = new LinkedList<>();
    private static List<Unit> myArchers = new LinkedList<>();

    private static List<Site> sitesReadyToTrain = new LinkedList<>();
    private static List<Site> enemyTowers = new LinkedList<>();
    private static List<Site> myTowers = new LinkedList<>();
    private static List<Site> myArcherBarracks = new LinkedList<>();
    private static List<Site> myKnightBarracks = new LinkedList<>();
    private static List<Site> sites = new LinkedList<>();

    private static int gold = 0;

    private static Boolean iAmBlue = null;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int numSites = in.nextInt();

        sites = new LinkedList<>();
        for (int i = 0; i < numSites; i++) {
            sites.add(new Site(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()));
        }

        // game loop
        while (true) {
            gold = in.nextInt();
            int touchedSite = in.nextInt(); // -1 if none

            updateSites(in);
            updateUnits(in);

            buildBuildingsOffence();
            trainUnitsOffence();

            gameTurns++;
            System.err.println("gameTurns: " + gameTurns);
        }
    }

    private static void updateSites(Scanner in) {
        sitesReadyToTrain = new LinkedList<>();
        enemyTowers = new LinkedList<>();
        myTowers = new LinkedList<>();
        myArcherBarracks = new LinkedList<>();
        myKnightBarracks = new LinkedList<>();

        sites.forEach(site -> {
            site.getSiteStatus().setSiteId(in.nextInt());
            site.getSiteStatus().setGold(in.nextInt());
            site.getSiteStatus().setMaxMineSize(in.nextInt());
            site.getSiteStatus().setStructureType(in.nextInt());
            site.getSiteStatus().setOwner(in.nextInt());
            site.getSiteStatus().setParam1(in.nextInt());
            site.getSiteStatus().setUnitType(in.nextInt());

            if (site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY) &&
                    site.getSiteStatus().getStructureType().equals(StructureType.BARRACKS)) {
                if (site.getSiteStatus().getUnitType().equals(UnitType.ARCHER)) {
                    myArcherBarracks.add(site);
                } else if (site.getSiteStatus().getUnitType().equals(UnitType.KNIGHT)) {
                    myKnightBarracks.add(site);
                }

                if (site.getSiteStatus().getParam1() == 0) {
                    sitesReadyToTrain.add(site);
                }
            } else if (site.getSiteStatus().getStructureType().equals(StructureType.TOWER) &&
                    site.getSiteStatus().getOwner().equals(OwnerType.ENEMY)) {
                enemyTowers.add(site);
            } else if (site.getSiteStatus().getStructureType().equals(StructureType.TOWER) &&
                    site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY)) {
                myTowers.add(site);
            }
        });
    }

    private static void updateUnits(Scanner in) {
        units = new LinkedList<>();
        enemyKnights = new LinkedList<>();
        myGiants = new LinkedList<>();
        myArchers = new LinkedList<>();

        numUnits = in.nextInt();
        System.err.println("numUnits: " + numUnits);
        for (int i = 0; i < numUnits; i++) {
            Unit unit = new Unit(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
            units.add(unit);

            if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.QUEEN)) {
                ourQueen = unit;
                System.err.println("ourQueen: " + ourQueen);

                if (iAmBlue == null) {
                    double distanceFromTopLeft = Math.abs(ourQueen.position.distance(0, 0));
                    double distanceFromBottomRight = Math.abs(ourQueen.position.distance(1920, 1000));

                    iAmBlue = distanceFromTopLeft > distanceFromBottomRight;
                }
            } else if (unit.getOwner().equals(OwnerType.ENEMY) && unit.getUnitType().equals(UnitType.QUEEN)) {
                theirQueen = unit;
            } else if (unit.getOwner().equals(OwnerType.ENEMY) && unit.getUnitType().equals(UnitType.KNIGHT)) {
                enemyKnights.add(unit);
            } else if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.GIANT)) {
                myGiants.add(unit);
            } else if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.ARCHER)) {
                myArchers.add(unit);
            }
        }
    }

    private static void buildBuildingsOffence() {
        String order = "MOVE " + ((int) theirQueen.getPosition().getX()) + " " + ((int) theirQueen.getPosition().getY());

        if (myKnightBarracks.size() < 1) {
            int radiusToBuildBuilding = 163813;
            int radiusToBuildBuildingCloseToEnemyQueen = 963813;

            Optional<Site> closestNonFriendlySite = sites.stream()
                    .filter(distanceIsBelow(ourQueen, radiusToBuildBuilding))
                    .filter(distanceIsBelow(theirQueen, radiusToBuildBuildingCloseToEnemyQueen))
                    .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                    .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                    .min(distanceTo(ourQueen.getPosition()));

            if (closestNonFriendlySite.isPresent()) {
                order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " BARRACKS-KNIGHT";
            } else {
                Optional<Site> mineLocation = sites.stream()
                        .filter(distanceIsBelow(ourQueen, radiusToBuildBuilding))
                        .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                        .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                        .min(distanceTo(ourQueen.getPosition()));
                if (mineLocation.isPresent()) {
                    order = "BUILD " + mineLocation.get().getSiteId() + " MINE";
                }
            }
        } else {
            int radiusToBuildBuilding = 103813;

            Optional<Site> closestNonFriendlySite = sites.stream()
                    .filter(distanceIsBelow(ourQueen, radiusToBuildBuilding))
                    .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY) ||
                            (site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY) &&
                                    site.getSiteStatus().getStructureType().equals(StructureType.MINE) &&
                                    site.getSiteStatus().getMaxMineSize() > site.getSiteStatus().getParam1())
                    )
                    .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                    .min(distanceTo(ourQueen.getPosition()));

            if (closestNonFriendlySite.isPresent()) {
                order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " MINE";
            } else if (iAmBlue) {
                order = "MOVE 1920 0";
            } else {
                order = "MOVE 0 1000";
            }
        }

        System.out.println(order);
    }

    private static void trainUnitsOffence() {
        List<Integer> goldUsed = new LinkedList<>();

        int radiusNotToSpawnFromEnemyQueen = 43813;

        System.out.println(sitesReadyToTrain.stream()
                .filter(distanceIsAbove(theirQueen, radiusNotToSpawnFromEnemyQueen))
                .filter(site -> site.getSiteStatus().getUnitType().equals(UnitType.KNIGHT))
                .filter(canPayForTraining(gold, goldUsed))
                .sorted(distanceTo(theirQueen.getPosition()))
                .map(Site::getSiteId)
                .map(String::valueOf)
                .collect(Collectors.joining(" ", "TRAIN ", ""))
                .trim());
    }

    private static Predicate<HasPosition> distanceIsBelow(Unit theirQueen, int radiusToSpawnKnights) {
        return site -> radiusToSpawnKnights > Math.abs(theirQueen.getPosition().distanceSq(site.getPosition()));
    }

    private static Predicate<HasPosition> distanceIsAbove(Unit theirQueen, int radiusNotToSpawnFromEnemyQueen) {
        return site -> radiusNotToSpawnFromEnemyQueen < Math.abs(theirQueen.getPosition().distanceSq(site.getPosition()));
    }

    private static Predicate<Site> canPayForTraining(int gold, List<Integer> goldUsed) {
        return site -> {
            int costToTrain = site.getSiteStatus().getUnitType().costToTrain;
            Integer sumOfGoldUsed = goldUsed.stream().reduce(0, Integer::sum);
            if (sumOfGoldUsed + costToTrain <= gold) {
                goldUsed.add(costToTrain);
                return true;
            } else {
                return false;
            }
        };
    }

    private static Comparator<Site> distanceTo(Point point) {
        return (site1, site2) -> {
            double site1DistanseToQueen = point.distanceSq(site1.getPosition());
            double site2DistanseToQueen = point.distanceSq(site2.getPosition());
            return (int) (Math.abs(site1DistanseToQueen) - Math.abs(site2DistanseToQueen));
        };
    }
}