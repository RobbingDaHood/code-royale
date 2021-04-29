import Models.Site;
import Models.Unit;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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

                if (site.getSiteStatus().getTurnsBeforeTraining() == 0) {
                    sitesReadyToTrain.add(site);
                }
            });

            int numUnits = in.nextInt();
            List<Unit> units = new LinkedList<>();
            for (int i = 0; i < numUnits; i++) {
                units.add(new Unit(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()));
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // First line: A valid queen action
            // Second line: A set of training instructions
            System.out.println("WAIT");
            System.out.println("TRAIN");

        }
    }
}