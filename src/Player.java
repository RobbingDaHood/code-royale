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
    private static List<Site> sites = new LinkedList<>();

    private static int gold = 0;

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

        sites.forEach(site -> {
            site.getSiteStatus().setSiteId(in.nextInt());
            site.getSiteStatus().setIgnore1(in.nextInt());
            site.getSiteStatus().setIgnore2(in.nextInt());
            site.getSiteStatus().setStructureType(in.nextInt());
            site.getSiteStatus().setOwner(in.nextInt());
            site.getSiteStatus().setTurnsBeforeTraining(in.nextInt());
            site.getSiteStatus().setUnitType(in.nextInt());

            if (site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY) &&
                    site.getSiteStatus().getStructureType().equals(StructureType.BARRACKS)) {
                if (site.getSiteStatus().getUnitType().equals(UnitType.ARCHER)) {
                    myArcherBarracks.add(site);
                }

                if (site.getSiteStatus().getTurnsBeforeTraining() == 0) {
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

    private static void buildBuildingsDefence() {
        String order = "MOVE " + ((int) theirQueen.getPosition().getX()) + " " + ((int) theirQueen.getPosition().getY());

        if (ourQueen.health < theirQueen.health - 10 && !myTowers.isEmpty()) {
            Optional<Site> closestFriendlyTower = myTowers.stream()
                    .min(distanceTo(ourQueen.getPosition()));
            order = "BUILD " + closestFriendlyTower.get().getSiteId() + " TOWER";
        } else {
            int sensitiveZoneAroundOurQueen = 563813;
            int radiusToBuildBuilding = 63813;
            if (!enemyTowers.isEmpty()) {
                radiusToBuildBuilding *= 22;
            }


            Optional<Site> closestNonFriendlySite = sites.stream()
                    .filter(distanceIsBelow(ourQueen, radiusToBuildBuilding))
                    .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                    .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                    .min(distanceTo(ourQueen.getPosition()));

            if (closestNonFriendlySite.isPresent()) {
                long enemyKnigthsCloseToQueen = units.stream()
                        .filter(distanceIsBelow(ourQueen, sensitiveZoneAroundOurQueen))
                        .filter(unit -> unit.getOwner().equals(OwnerType.ENEMY))
                        .filter(unit -> unit.getUnitType().equals(UnitType.KNIGHT))
                        .count();

                if (myArcherBarracks.size() < 1) {
                    order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " BARRACKS-ARCHER";
                } else if (enemyKnigthsCloseToQueen > 5) {
                    order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " TOWER";
                } else {
                    UnitType typeToBuildNext =
                            Stream.concat(
                                    Arrays.stream(UnitType.values()).sequential()
                                            .filter(ut -> !ut.equals(UnitType.QUEEN)),
                                    sites.stream()
                                            .filter(site -> site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                                            .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                                            .map(site -> site.getSiteStatus().getUnitType()))
                                    .collect(Collectors.groupingBy(ut -> ut))
                                    .entrySet().stream()
                                    .filter(entry -> !entry.getKey().equals(UnitType.GIANT) || entry.getValue().size() < 2)
                                    .min(Comparator.comparingInt(entry -> entry.getValue().size()))
                                    .map(Map.Entry::getKey)
                                    .orElse(UnitType.ARCHER);

                    order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " BARRACKS-" + typeToBuildNext;

                }
            }
        }

        System.out.println(order);
    }

    private static void buildBuildingsOffence() {
        String order = "MOVE " + ((int) theirQueen.getPosition().getX()) + " " + ((int) theirQueen.getPosition().getY());


        int sensitiveZoneAroundOurQueen = 563813;
        int radiusToBuildBuilding = 63813;

        System.err.println("ourQueen: " + ourQueen);
        Optional<Site> closestNonFriendlySite = sites.stream()
                .filter(distanceIsBelow(ourQueen, radiusToBuildBuilding))
                .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                .min(distanceTo(ourQueen.getPosition()));

        if (closestNonFriendlySite.isPresent()) {
            List<Site> ourBarracks = sites.stream()
                    .filter(site -> site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                    .filter(site -> site.getSiteStatus().getStructureType().equals(StructureType.BARRACKS))
                    .collect(Collectors.toList());
//            long enemyKnigthsCloseToQueen = units.stream()
//                    .filter(distanceIsBelow(ourQueen, sensitiveZoneAroundOurQueen))
//                    .filter(unit -> unit.getOwner().equals(OwnerType.ENEMY))
//                    .filter(unit -> unit.getUnitType().equals(UnitType.KNIGHT))
//                    .count();
//
//            Optional<Site> closestFriendlyTower = myTowers.stream()
//                    .min(distanceTo(ourQueen.getPosition()));
//            if (enemyKnigthsCloseToQueen > 3 && closestFriendlyTower.isPresent() && ourBarracks.size() >= 2) {
//                order = "BUILD " + closestFriendlyTower.get().getSiteId() + " TOWER";
//            } else
            if (myTowers.size() <= ourBarracks.size()) {
                order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " TOWER";
            } else {
                order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " BARRACKS-KNIGHT";
            }
        }

        System.out.println(order);
    }

    private static void trainUnitsDefence() {
        List<Integer> goldUsed = new LinkedList<>();

        int radiusToSpawnKnights = 563813;
        int radiusToSpawnArchers = 563813;
        int radiusNotToSpawnFromEnemyQueen = 43813;

        List<String> giants = new LinkedList<>();
        if (!enemyTowers.isEmpty() && myGiants.size() < 1 && myArchers.size() > 3) {
            giants = sitesReadyToTrain.stream()
                    .filter(distanceIsAbove(theirQueen, radiusNotToSpawnFromEnemyQueen))
                    .filter(site -> site.getSiteStatus().getUnitType().equals(UnitType.GIANT))
                    .filter(canPayForTraining(gold, goldUsed))
                    .sorted(distanceTo(enemyTowers.get(0).getPosition()))
                    .map(Site::getSiteId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        }

        List<String> defenceArcher = new LinkedList<>();
        List<String> killerKnightSides = new LinkedList<>();
        if (!enemyKnights.isEmpty() && myArchers.size() < enemyKnights.size() + 3 && gold >= UnitType.GIANT.costToTrain) {
            defenceArcher = sitesReadyToTrain.stream()
                    .filter(distanceIsAbove(theirQueen, radiusNotToSpawnFromEnemyQueen))
                    .filter(site -> site.getSiteStatus().getUnitType().equals(UnitType.ARCHER))
                    .filter(distanceIsBelow(ourQueen, radiusToSpawnArchers))
                    .filter(canPayForTraining(gold, goldUsed))
                    .sorted(distanceTo(ourQueen.getPosition()))
                    .map(Site::getSiteId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } else if (gold >= UnitType.GIANT.costToTrain) {
            killerKnightSides = sitesReadyToTrain.stream()
                    .filter(distanceIsAbove(theirQueen, radiusNotToSpawnFromEnemyQueen))
                    .filter(site -> site.getSiteStatus().getUnitType().equals(UnitType.KNIGHT))
                    .filter(distanceIsBelow(theirQueen, radiusToSpawnKnights))
                    .filter(canPayForTraining(gold, goldUsed))
                    .sorted(distanceTo(theirQueen.getPosition()))
                    .map(Site::getSiteId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        }


        System.out.println(
                Stream.of(killerKnightSides.stream(), defenceArcher.stream(), giants.stream())
                        .flatMap(i -> i)
                        .collect(Collectors.joining(" ", "TRAIN ", ""))
                        .trim());
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
                .trim()));
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