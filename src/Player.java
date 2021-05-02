import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private static final int mapHeight = 1000;
    private static final int mapWidth = 1920;
    private static int gameTurns = 0;
    private static int numUnits = 0;
    private static Unit ourQueen = null;
    private static Unit theirQueen = null;
    private static List<Unit> units = new LinkedList<>();
    private static List<Unit> enemyKnights = new LinkedList<>();
    private static List<Unit> myGiants = new LinkedList<>();
    private static List<Unit> myArchers = new LinkedList<>();
    private static List<Unit> enemyArchers = new LinkedList<>();
    private static List<Site> sitesReadyToTrain = new LinkedList<>();
    private static List<Site> enemyTowers = new LinkedList<>();
    private static List<Site> myTowers = new LinkedList<>();
    private static List<Site> myMines = new LinkedList<>();
    private static List<Site> myArcherBarracks = new LinkedList<>();
    private static List<Site> myKnightBarracks = new LinkedList<>();
    private static List<Site> myGiantBarracks = new LinkedList<>();
    private static List<Site> sites = new LinkedList<>();
    private static int gold = 0;
    private static int goldIncome = 0;
    private static Boolean iAmBlue = null;
    private static NavMeshIsh2D navMeshIsh2D = null;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int numSites = in.nextInt();

        sites = new LinkedList<>();
        for (int i = 0; i < numSites; i++) {
            sites.add(new Site(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()));
        }

        // game loop
        while (true) {
            navMeshIsh2D = new NavMeshIsh2D(getZoneCoordinate(mapWidth) + 1, getZoneCoordinate(mapHeight) + 1);
            gold = in.nextInt();
            int touchedSite = in.nextInt(); // -1 if none

            updateSites(in);
            updateUnits(in);
            navMeshIsh2D.printPosition(getZoneCoordinate(ourQueen.position), 5);

            //Handle many towers (Build some giants?)
            //Handle many knights (Build some towers?)
            //Handle many archers (Wait with building units?)
            //Else build many mines and spam knights
            //Maybe queen movement is still the same.

            buildBuildingsOffence();
            trainUnitsOffence();

            gameTurns++;
            System.err.println("gameTurns: " + gameTurns);
        }
    }

    private static int getZoneCoordinate(int mapCoordinate) {
        return mapCoordinate / 50;
    }

    private static int getMapCoordinate(int zoneCoordine) {
        return zoneCoordine * 50;
    }

    private static Point getZoneCoordinate(Point mapCoordinate) {
        return new Point(getZoneCoordinate(mapCoordinate.x), getZoneCoordinate(mapCoordinate.y));
    }

    private static Point getMapCoordinate(Point zoneCoordine) {
        return new Point(getMapCoordinate(zoneCoordine.x), getMapCoordinate(zoneCoordine.y));
    }

    private static void updateSites(Scanner in) {
        sitesReadyToTrain = new LinkedList<>();
        enemyTowers = new LinkedList<>();
        myTowers = new LinkedList<>();
        myArcherBarracks = new LinkedList<>();
        myKnightBarracks = new LinkedList<>();
        myGiantBarracks = new LinkedList<>();
        goldIncome = 0;

        sites.forEach(site -> {
            site.getSiteStatus().setSite(site);
            site.getSiteStatus().setSiteId(in.nextInt());
            site.getSiteStatus().setGold(in.nextInt());
            site.getSiteStatus().setMaxMineSize(in.nextInt());
            site.getSiteStatus().setStructureType(in.nextInt());
            site.getSiteStatus().setOwner(in.nextInt());
            site.getSiteStatus().setParam1(in.nextInt());
            site.getSiteStatus().setUnitTypeOrTowerRange(in.nextInt());

            if (site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY) &&
                    site.getSiteStatus().getStructureType().equals(StructureType.BARRACKS)) {
                if (site.getSiteStatus().getUnitType().equals(UnitType.ARCHER)) {
                    myArcherBarracks.add(site);
                } else if (site.getSiteStatus().getUnitType().equals(UnitType.KNIGHT)) {
                    myKnightBarracks.add(site);
                } else if (site.getSiteStatus().getUnitType().equals(UnitType.GIANT)) {
                    myGiantBarracks.add(site);
                }

                if (site.getSiteStatus().getParam1() == 0) {
                    sitesReadyToTrain.add(site);
                }

                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 9999, 0, getZoneCoordinate(site.getRadius()), true);
            } else if (site.getSiteStatus().getStructureType().equals(StructureType.TOWER) &&
                    site.getSiteStatus().getOwner().equals(OwnerType.ENEMY)) {
                enemyTowers.add(site);
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 500, 10, getZoneCoordinate(site.getSiteStatus().getTowerRange()), true);
            } else if (site.getSiteStatus().getStructureType().equals(StructureType.TOWER) &&
                    site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY)) {
                myTowers.add(site);
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 9999, 0, getZoneCoordinate(site.getRadius()), true);
            } else if (site.getSiteStatus().getStructureType().equals(StructureType.MINE) &&
                    site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY)) {
                myMines.add(site);
                goldIncome += site.getSiteStatus().getParam1();
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 9999, 0, getZoneCoordinate(site.getRadius()), true);
            } else if (!site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY)) {
//                System.err.println("site.position: " + site.position);
//                System.err.println("getZoneCoordinate(site.position): " + getZoneCoordinate(site.position));
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 500, 10, getZoneCoordinate(mapWidth*2), false);
            } else {
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 9999, 0, getZoneCoordinate(site.getRadius()), true);
            }
        });
    }

    private static void updateUnits(Scanner in) {
        units = new LinkedList<>();
        enemyKnights = new LinkedList<>();
        myGiants = new LinkedList<>();
        myArchers = new LinkedList<>();
        enemyArchers = new LinkedList<>();

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
            } else if (unit.getOwner().equals(OwnerType.ENEMY) && unit.getUnitType().equals(UnitType.ARCHER)) {
                enemyArchers.add(unit);
            } else if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.GIANT)) {
                myGiants.add(unit);
            } else if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.ARCHER)) {
                myArchers.add(unit);
            }
        }
    }

    private static void buildBuildingsOffence() {
        String order = "MOVE " + ((int) theirQueen.getPosition().getX()) + " " + ((int) theirQueen.getPosition().getY());

        List<Unit> threadeningKnights = units.stream()
                .filter(unit -> unit.owner.equals(OwnerType.ENEMY))
                .filter(unit -> unit.unitType.equals(UnitType.KNIGHT))
                .filter(unit -> distanceIsBelow(ourQueen, unit.getThreadArea(), unit.getPosition()))
                .collect(Collectors.toList());

//        if (threadeningKnights.size() > 0) { //Run away and change everything to towers
//
//            List<Unit> threadeningKnights = units.stream()
//                    .map(unit -> unit.position.)
//        }


        if (gold > 120 && myGiantBarracks.size() < 1) {
            int radiusToBuildBuilding = 500;

            Optional<Site> closestNonFriendlySite = sites.stream()
                    .filter(distanceIsBelow(ourQueen, radiusToBuildBuilding))
                    .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                    .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                    .min(distanceTo(ourQueen.getPosition()));
            if (closestNonFriendlySite.isPresent()) {
                order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " BARRACKS-GIANT";
            }
        } else if (myKnightBarracks.size() < 1) {
            int radiusToBuildBuilding = 300;
            int radiusToBuildBuildingCloseToEnemyQueen = 1000;

            Optional<Site> closestNonFriendlySite = sites.stream()
                    .filter(distanceIsBelow(ourQueen, radiusToBuildBuilding))
                    .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                    .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                    .min(distanceTo(ourQueen.getPosition()));

            if (closestNonFriendlySite.isPresent() && distanceIsBelow(theirQueen, radiusToBuildBuildingCloseToEnemyQueen).test(closestNonFriendlySite.get())) {
                order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " BARRACKS-KNIGHT";
            } else {
                closestNonFriendlySite = sites.stream()
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
                }
            }
        } else {
            int radiusToBuildBuilding = 500;

            order = sites.stream()
                    .filter(distanceIsBelow(ourQueen, radiusToBuildBuilding))
                    .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                    .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                    .min(distanceTo(ourQueen.getPosition()))
                    .map(site -> "BUILD " + site.getSiteId() + " TOWER")
                    .orElseGet(() ->
                            myTowers.stream()
                                    .filter(site -> site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                                    .filter(site -> site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                                    .min(distanceTo(iAmBlue ? new Point(1920, 0) : new Point(0, 1000)))
                                    .map(site -> "BUILD " + site.getSiteId() + " TOWER")
                                    .orElseGet(() ->
                                            iAmBlue ? "MOVE 1920 0" : "MOVE 0 1000"
                                    )
                    );
        }

        System.out.println(order);
    }

    private static void trainUnitsOffence() {
        List<Integer> goldUsed = new LinkedList<>();

        int radiusNotToSpawnFromEnemyQueen = 100;

        Stream<String> giantSites = Stream.empty();
        if (myGiants.size() < enemyTowers.size() / 2) {
            giantSites = sitesReadyToTrain.stream()
                    .filter(distanceIsAbove(theirQueen, radiusNotToSpawnFromEnemyQueen))
                    .filter(site -> site.getSiteStatus().getUnitType().equals(UnitType.GIANT))
                    .filter(canPayForTraining(gold, goldUsed))
                    .sorted(distanceTo(theirQueen.getPosition()))
                    .map(Site::getSiteId)
                    .map(String::valueOf);
            System.err.println("giantSites: ");
        }

        System.err.println("enemyTowers.size(): " + enemyTowers.size());
        Stream<String> knightSites = Stream.empty();
        if (enemyArchers.size() < 6 && (enemyTowers.size() == 0 || myGiants.size() >= enemyTowers.size() / 2)) {
            knightSites = sitesReadyToTrain.stream()
                    .filter(distanceIsAbove(theirQueen, radiusNotToSpawnFromEnemyQueen))
                    .filter(site -> site.getSiteStatus().getUnitType().equals(UnitType.KNIGHT))
                    .filter(canPayForTraining(gold, goldUsed))
                    .sorted(distanceTo(theirQueen.getPosition()))
                    .map(Site::getSiteId)
                    .map(String::valueOf);
            System.err.println("knightSites: ");
        }

        System.out.println(
                Stream.of(knightSites, giantSites)
                        .flatMap(i -> i)
                        .collect(Collectors.joining(" ", "TRAIN ", ""))
                        .trim());
    }

    private static Predicate<HasPosition> distanceIsBelow(Unit theirQueen, int radiusToSpawnKnights) {
        return site -> distanceIsBelow(theirQueen, radiusToSpawnKnights, site.getPosition());
    }

    private static boolean distanceIsBelow(Unit theirQueen, int radiusToSpawnKnights, Point point) {
        return radiusToSpawnKnights > Math.abs(theirQueen.getPosition().distance(point));
    }

    private static Predicate<HasPosition> distanceIsAbove(Unit theirQueen, int radiusNotToSpawnFromEnemyQueen) {
        return site -> radiusNotToSpawnFromEnemyQueen < Math.abs(theirQueen.getPosition().distance(site.getPosition()));
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