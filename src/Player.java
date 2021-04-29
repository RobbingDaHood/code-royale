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

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int numSites = in.nextInt();

        List<Site> sites = new LinkedList<>();
        for (int i = 0; i < numSites; i++) {
            sites.add(new Site(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()));
        }


        // game loop
        while (true) {
            int gold = in.nextInt();
            int touchedSite = in.nextInt(); // -1 if none

            List<Site> sitesReadyToTrain = new LinkedList<>();
            sites.forEach(site -> {
                site.getSiteStatus().setSiteId(in.nextInt());
                site.getSiteStatus().setIgnore1(in.nextInt());
                site.getSiteStatus().setIgnore2(in.nextInt());
                site.getSiteStatus().setStructureType(in.nextInt());
                site.getSiteStatus().setOwner(in.nextInt());
                site.getSiteStatus().setTurnsBeforeTraining(in.nextInt());
                site.getSiteStatus().setUnitType(in.nextInt());

                if (site.getSiteStatus().getTurnsBeforeTraining() == 0 &&
                        site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY) &&
                        site.getSiteStatus().getStructureType().equals(StructureType.BARRACKS)) {
                    sitesReadyToTrain.add(site);
                }
            });

            int numUnits = in.nextInt();
            List<Unit> units = new LinkedList<>();
            Unit ourQueen = null;
            Unit theirQueen = null;
            List<Unit> enemyKnights = new LinkedList<>();
            for (int i = 0; i < numUnits; i++) {
                Unit unit = new Unit(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
                units.add(unit);

                if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.QUEEN)) {
                    ourQueen = unit;
                } else if (unit.getOwner().equals(OwnerType.ENEMY) && unit.getUnitType().equals(UnitType.QUEEN)) {
                    theirQueen = unit;
                } else if (unit.getOwner().equals(OwnerType.ENEMY) && unit.getUnitType().equals(UnitType.KNIGHT)) {
                    enemyKnights.add(unit);
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            buildBuildings(sites, ourQueen, theirQueen);
            trainUnits(sitesReadyToTrain, ourQueen, gold, theirQueen, enemyKnights);
        }
    }

    private static void buildBuildings(List<Site> sites, Unit finalOurQueen, Unit theirQueen) {
        String order = "MOVE " + ((int) theirQueen.getPosition().getX()) + " " + ((int) theirQueen.getPosition().getY());

        int radiusToBuildBuilding = 63813;
        Optional<Site> closestNonFriendlySite = sites.stream()
                .filter(distanceIsBelow(finalOurQueen, radiusToBuildBuilding))
                .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                .min(distanceTo(finalOurQueen.getPosition()));


        if (closestNonFriendlySite.isPresent()) {
            UnitType typeToBuildNext =
                    Stream.concat(
                            Arrays.stream(UnitType.values()).sequential()
                                    .filter(ut -> !ut.equals(UnitType.QUEEN)),
                            sites.stream()
                                    .filter(site -> site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                                    .map(site -> site.getSiteStatus().getUnitType()))
                            .collect(Collectors.groupingBy(ut -> ut))
                            .entrySet().stream()
                            .min(Comparator.comparingInt(entry -> entry.getValue().size()))
                            .map(Map.Entry::getKey)
                            .orElse(UnitType.ARCHER);

            order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " BARRACKS-" + typeToBuildNext;
        }

        System.out.println(order);
    }

    private static void trainUnits(List<Site> sitesReadyToTrain, Unit finalOurQueen, int gold, Unit theirQueen, List<Unit> enemyKnights) {
        List<Integer> goldUsed = new LinkedList<>();

        int radiusToSpawnKnights = 563813;
        int radiusToSpawnArchers = 563813;
        int radiusNotToSpawnFromEnemyQueen = 43813;

        List<String> killerKnightSides = sitesReadyToTrain.stream()
                .filter(distanceIsAbove(theirQueen, radiusNotToSpawnFromEnemyQueen))
                .filter(site -> site.getSiteStatus().getUnitType().equals(UnitType.KNIGHT))
                .filter(distanceIsBelow(theirQueen, radiusToSpawnKnights))
                .filter(canPayForTraining(gold, goldUsed))
                .sorted(distanceTo(theirQueen.getPosition()))
                .map(Site::getSiteId)
                .map(String::valueOf)
                .collect(Collectors.toList());

        List<String> defenceArcher = new LinkedList<>();
        Integer goldUsedSum = goldUsed.stream().reduce(0, Integer::sum);
        if (goldUsedSum + (UnitType.ARCHER.costToTrain * 2) <= gold && !enemyKnights.isEmpty()) {
            defenceArcher.addAll(sitesReadyToTrain.stream()
                    .filter(distanceIsAbove(theirQueen, radiusNotToSpawnFromEnemyQueen))
                    .filter(site -> site.getSiteStatus().getUnitType().equals(UnitType.ARCHER))
                    .filter(distanceIsBelow(finalOurQueen, radiusToSpawnArchers))
                    .filter(canPayForTraining(gold, goldUsed))
                    .sorted(distanceTo(finalOurQueen.getPosition()))
                    .map(Site::getSiteId)
                    .map(String::valueOf)
                    .collect(Collectors.toList()));
        }

        System.out.println(Stream.concat(killerKnightSides.stream(), defenceArcher.stream())
                .collect(Collectors.joining(" ", "TRAIN ", ""))
                .trim());
    }

    private static Predicate<Site> distanceIsBelow(Unit theirQueen, int radiusToSpawnKnights) {
        return site -> radiusToSpawnKnights > Math.abs(theirQueen.getPosition().distanceSq(site.getPosition()));
    }

    private static Predicate<Site> distanceIsAbove(Unit theirQueen, int radiusNotToSpawnFromEnemyQueen) {
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