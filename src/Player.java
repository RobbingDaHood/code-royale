import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {


    private static UnitType typeToBuildNext = UnitType.ARCHER;

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
            for (int i = 0; i < numUnits; i++) {
                Unit unit = new Unit(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
                units.add(unit);

                if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.QUEEN)) {
                    ourQueen = unit;
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            buildBuildings(sites, ourQueen);
            trainUnits(sitesReadyToTrain, ourQueen, gold);
        }
    }

    private static void buildBuildings(List<Site> sites, Unit finalOurQueen) {
        Site closestEmptySite = sites.stream()
                .filter(site -> site.getSiteStatus().getStructureType().equals(StructureType.NO_STRUCTURE))
                .min((site1, site2) -> {
                    double site1DistanseToQueen = finalOurQueen.getPosition().distanceSq(site1.getPosition());
                    double site2DistanseToQueen = finalOurQueen.getPosition().distanceSq(site2.getPosition());
                    return (int) (Math.abs(site1DistanseToQueen) - Math.abs(site2DistanseToQueen));
                })
                .get();

        // First line: A valid queen action
        // Second line: A set of training instructions
        System.out.println("BUILD " + closestEmptySite.getSiteId() + " BARRACKS-" + typeToBuildNext);

        //Switch type to build:
        if (typeToBuildNext == UnitType.KNIGHT) {
            typeToBuildNext = UnitType.ARCHER;
        } else if (typeToBuildNext == UnitType.ARCHER) {
            typeToBuildNext = UnitType.KNIGHT;
        }
    }

    private static void trainUnits(List<Site> sitesReadyToTrain, Unit finalOurQueen, int gold) {
        List<Integer> goldUsed = new LinkedList<>();

        String closestTrainableSite = sitesReadyToTrain.stream()
                .sorted((site1, site2) -> {
                    double site1DistanseToQueen = finalOurQueen.getPosition().distanceSq(site1.getPosition());
                    double site2DistanseToQueen = finalOurQueen.getPosition().distanceSq(site2.getPosition());
                    return (int) (Math.abs(site1DistanseToQueen) - Math.abs(site2DistanseToQueen));
                })
                .filter(site -> {
                    int costToTrain = site.getSiteStatus().getUnitType().costToTrain;
                    Integer sumOfGoldUsed = goldUsed.stream().reduce(0, Integer::sum);
                    if (sumOfGoldUsed + costToTrain <= gold) {
                        goldUsed.add(costToTrain);
                        return true;
                    } else {
                        return false;
                    }
                })
                .map(Site::getSiteId)
                .map(String::valueOf)
                .collect(Collectors.joining(" ", "TRAIN ", ""));

        System.out.println(closestTrainableSite.trim());
    }
}