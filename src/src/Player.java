package src;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    private static List<Unit> myKnights = new LinkedList<>();
    private static List<Unit> myGiants = new LinkedList<>();
    private static List<Unit> myArchers = new LinkedList<>();
    private static List<Unit> enemyArchers = new LinkedList<>();
    private static List<Site> sitesReadyToTrain = new LinkedList<>();
    private static List<Site> enemyTowers = new LinkedList<>();
    private static List<Site> myTowers = new LinkedList<>();
    private static List<Site> myMines = new LinkedList<>();
    private static List<Site> enemyMines = new LinkedList<>();
    private static List<Site> myArcherBarracks = new LinkedList<>();
    private static List<Site> myKnightBarracks = new LinkedList<>();
    private static List<Site> myGiantBarracks = new LinkedList<>();
    private static List<Site> enemyKnightBarracks = new LinkedList<>();
    private static List<Site> sites = new LinkedList<>();
    private static int gold = 0;
    private static int goldIncome = 0;
    private static Boolean iAmBlue = null;
    private static NavMeshIsh2D navMeshIsh2D = null;

    private static boolean isBursting = false;

    private static int granularity = 100;
    private static boolean hideBehindFort = false;

    private static GameStates gameState = GameStates.STARTING;

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
//            navMeshIsh2D.printPosition(getZoneCoordinate(ourQueen.position), 1);

            //Handle many towers (Build some giants?)
            //Handle many knights (Build some towers?)
            //Handle many archers (Wait with building units?)
            //Else build many mines and spam knights
            //Maybe queen movement is still the same.

            buildBuildingsOffenceII();
            trainUnitsOffence();

            gameTurns++;
            System.err.println("gameTurns: " + gameTurns);
        }
    }

    private static int getZoneCoordinate(int mapCoordinate) {
        return mapCoordinate / granularity;
    }

    private static int getMapCoordinate(int zoneCoordine) {
        return zoneCoordine * granularity + granularity / 2;
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
        enemyKnightBarracks = new LinkedList<>();
        myMines = new LinkedList<>();
        enemyMines = new LinkedList<>();
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

                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 99999, 0, getZoneCoordinate(site.getRadius()), NavMeshMapTypes.BLOCKER);
            } else if (site.getSiteStatus().getOwner().equals(OwnerType.ENEMY) &&
                    site.getSiteStatus().getStructureType().equals(StructureType.BARRACKS)) {
                if (site.getSiteStatus().getUnitType().equals(UnitType.KNIGHT)) {
                    enemyKnightBarracks.add(site);
                }
            } else if (site.getSiteStatus().getStructureType().equals(StructureType.TOWER) &&
                    site.getSiteStatus().getOwner().equals(OwnerType.ENEMY)) {
                enemyTowers.add(site);
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 99999, 0, getZoneCoordinate(site.getSiteStatus().getTowerRange()), NavMeshMapTypes.COST);
                navMeshIsh2D.insertValue(getZoneCoordinate(site.position), 9999, 1, getZoneCoordinate(500), NavMeshMapTypes.ENEMY_TOWER_RANGE, false);
            } else if (site.getSiteStatus().getStructureType().equals(StructureType.TOWER) &&
                    site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY)) {
                myTowers.add(site);
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 99999, 0, getZoneCoordinate(site.getRadius()), NavMeshMapTypes.BLOCKER);
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 20, 1, getZoneCoordinate(site.getSiteStatus().getTowerRange()), NavMeshMapTypes.TOWER_PROTECTION);
//                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), -2, 0, getZoneCoordinate(site.getRadius()), src.NavMeshMapTypes.ENEMY_KNIGHTS);
            } else if (site.getSiteStatus().getStructureType().equals(StructureType.MINE)) {
                if (site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY)) {
                    myMines.add(site);
                    goldIncome += site.getSiteStatus().getParam1();
                    navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 99999, 0, getZoneCoordinate(site.getRadius()), NavMeshMapTypes.BLOCKER);
                    navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 20, 1, getZoneCoordinate(500), NavMeshMapTypes.NON_TOWER_PLACES_TO_BUILD);
                } else {
                    enemyMines.add(site);
                }
            } else if (!site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY)) {
//                System.err.println("site.position: " + site.position);
//                System.err.println("getZoneCoordinate(site.position): " + getZoneCoordinate(site.position));
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 20, 1, getZoneCoordinate(500), NavMeshMapTypes.NON_FRIENDLY_PLACES_TO_BUILD);
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 20, 1, getZoneCoordinate(500), NavMeshMapTypes.NON_TOWER_PLACES_TO_BUILD);
            } else {
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 99999, 0, getZoneCoordinate(site.getRadius()), NavMeshMapTypes.BLOCKER);
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(site.position), 20, 1, getZoneCoordinate(500), NavMeshMapTypes.NON_TOWER_PLACES_TO_BUILD);
            }
        });
    }

    private static void updateUnits(Scanner in) {
        units = new LinkedList<>();
        enemyKnights = new LinkedList<>();
        myGiants = new LinkedList<>();
        myArchers = new LinkedList<>();
        enemyArchers = new LinkedList<>();
        myKnights = new LinkedList<>();

        numUnits = in.nextInt();
        System.err.println("numUnits: " + numUnits);
        for (int i = 0; i < numUnits; i++) {
            Unit unit = new Unit(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
            units.add(unit);

            if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.QUEEN)) {
                ourQueen = unit;

                if (iAmBlue == null) {
                    double distanceFromTopLeft = Math.abs(ourQueen.position.distance(0, 0));
                    double distanceFromBottomRight = Math.abs(ourQueen.position.distance(1920, 1000));

                    iAmBlue = distanceFromTopLeft > distanceFromBottomRight;
                }
            } else if (unit.getOwner().equals(OwnerType.ENEMY) && unit.getUnitType().equals(UnitType.QUEEN)) {
                theirQueen = unit;
            } else if (unit.getOwner().equals(OwnerType.ENEMY) && unit.getUnitType().equals(UnitType.KNIGHT)) {
                enemyKnights.add(unit);
                navMeshIsh2D.insertGradiantValue(getZoneCoordinate(unit.position), unit.health * 5, 5, getZoneCoordinate(500), NavMeshMapTypes.ENEMY_KNIGHTS);
            } else if (unit.getOwner().equals(OwnerType.ENEMY) && unit.getUnitType().equals(UnitType.ARCHER)) {
                enemyArchers.add(unit);
            } else if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.GIANT)) {
                myGiants.add(unit);
            } else if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.ARCHER)) {
                myArchers.add(unit);
            } else if (unit.getOwner().equals(OwnerType.FRIENDLY) && unit.getUnitType().equals(UnitType.KNIGHT)) {
                myKnights.add(unit);
            }
        }

        System.err.println("ourQueen: " + ourQueen.position);
        System.err.println("ourQueen: " + getZoneCoordinate(ourQueen.position));
        System.err.println("theirQueen: " + theirQueen.position);
        System.err.println("theirQueen: " + getZoneCoordinate(theirQueen.position));
        System.err.println("Distance between queens: " + Math.abs(ourQueen.position.distance(theirQueen.position)));

    }

    private static void buildBuildingsOffenceII() {
        System.err.println("Gamestate: " + gameState);

        if (gameState.equals(GameStates.STARTING)) {
            handleStarting();
        } else if (gameState.equals(GameStates.EXPAND)) {
            handleExpand();
        } else if (gameState.equals(GameStates.DEFEND_MODERATE)) {
            handleDefendModerate();
        } else if (gameState.equals(GameStates.DEFEND_IN_FORT)) {
            handleDefendInFort();
        }

    }

    private static void handleDefendInFort() {
        handleDefendInFortDo();

        boolean defence = navMeshIsh2D.maps.get(NavMeshMapTypes.ENEMY_KNIGHTS)
                [getZoneCoordinate(ourQueen.position.x)][getZoneCoordinate(ourQueen.position.y)] > 10;

//        if (!defence) {
//            gameState = GameStates.EXPAND;
//        } else {
//            HashMap<NavMeshMapTypes, Integer> towerDefenceWeight = new HashMap<NavMeshMapTypes, Integer>() {
//                {
//                    put(NavMeshMapTypes.TOWER_PROTECTION, 1);
//                }
//            };
//            navMeshIsh2D.printPosition(getZoneCoordinate(ourQueen.position), 1, towerDefenceWeight);
//            Point getBestTowerDefenceZone = navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position), towerDefenceWeight);
//
//            int towerDefenceValue = navMeshIsh2D.getCostBenefit(getBestTowerDefenceZone, towerDefenceWeight);
//
//            System.err.println("towerDefence: " + towerDefenceValue + ", " + getBestTowerDefenceZone);
//            if (towerDefenceValue > 50) {
//                gameState = GameStates.DEFEND_IN_FORT;
//            } else {
//                gameState = GameStates.DEFEND_MODERATE;
//            }
//        }
    }

    private static void handleDefendInFortDo() {
        String order;
        Point moveToThisPoint;

        Optional<Site> closestEnemyBaracks = enemyKnightBarracks.stream()
                .min(distanceTo(ourQueen.position));
        Optional<Site> defensiveTower = Optional.empty();

        HashMap<NavMeshMapTypes, Integer> typeWeights = new HashMap<NavMeshMapTypes, Integer>() {
            {
                put(NavMeshMapTypes.ENEMY_KNIGHTS, -2);
                put(NavMeshMapTypes.BLOCKER, -1);
                put(NavMeshMapTypes.TOWER_PROTECTION, 1);
            }
        };

        if (closestEnemyBaracks.isPresent()) {
            defensiveTower = myTowers.stream()
                    .max(distanceTo(closestEnemyBaracks.get().position));
        }

        Point getBestTowerDefenceZone;
        if (defensiveTower.isPresent()) {
            navMeshIsh2D.printPosition(getZoneCoordinate(defensiveTower.get().position), 1, typeWeights);
            getBestTowerDefenceZone = navMeshIsh2D.getBestNeighbour(getZoneCoordinate(defensiveTower.get().position), typeWeights);
        } else {
            navMeshIsh2D.printPosition(getZoneCoordinate(ourQueen.position), 1, typeWeights);
            getBestTowerDefenceZone = navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position), typeWeights);
        }

        moveToThisPoint = getMapCoordinate(getBestTowerDefenceZone);

        Optional<Site> closestFreindlyTowerSite = myTowers.stream()
                .filter(distanceIsBelow(moveToThisPoint, 300))
                .filter(site -> site.getSiteStatus().getStructureType().equals(StructureType.TOWER) &&
                        site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                .min(distanceTo(moveToThisPoint));

        if (closestFreindlyTowerSite.isPresent()) {
            order = "BUILD " + closestFreindlyTowerSite.get().getSiteId() + " TOWER";
        } else {
            order = "MOVE " + moveToThisPoint.x + " " + moveToThisPoint.y;
        }

        System.out.println(order);
    }

    private static void handleDefendModerate() {
        handleDefendModerateDo();
        handleDefendModerateChangeState();
    }

    private static void handleDefendModerateChangeState() {
        boolean defence = navMeshIsh2D.maps.get(NavMeshMapTypes.ENEMY_KNIGHTS)
                [getZoneCoordinate(ourQueen.position.x)][getZoneCoordinate(ourQueen.position.y)] > 10;

        if (!defence) {
            gameState = GameStates.EXPAND;
        } else {
            HashMap<NavMeshMapTypes, Integer> towerDefenceWeight = new HashMap<NavMeshMapTypes, Integer>() {
                {
                    put(NavMeshMapTypes.TOWER_PROTECTION, 1);
                }
            };
            navMeshIsh2D.printPosition(getZoneCoordinate(ourQueen.position), 1, towerDefenceWeight);
            Point getBestTowerDefenceZone = navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position), towerDefenceWeight);

            int towerDefenceValue = navMeshIsh2D.getCostBenefit(getBestTowerDefenceZone, towerDefenceWeight);

            System.err.println("towerDefence: " + towerDefenceValue + ", " + getBestTowerDefenceZone);
            if (towerDefenceValue > 50) {
                gameState = GameStates.DEFEND_IN_FORT;
            }
        }
    }

    private static void handleDefendModerateDo() {
        String order;

        Point moveToThisPoint = getMapCoordinate(navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position),
                new HashMap<NavMeshMapTypes, Integer>() {
                    {
                        put(NavMeshMapTypes.NON_FRIENDLY_PLACES_TO_BUILD, 1);
                        put(NavMeshMapTypes.ENEMY_KNIGHTS, -2);
                        put(NavMeshMapTypes.BLOCKER, -1);
                    }
                }));

        Optional<Site> closestNonFriendlySite = sites.stream()
                .filter(distanceIsBelow(moveToThisPoint, 600))
                .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                .min(distanceTo(ourQueen.position));

        if (closestNonFriendlySite.isPresent()) {
            order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " TOWER";
        } else {
            order = "MOVE " + moveToThisPoint.x + " " + moveToThisPoint.y;
        }

        System.out.println(order);
    }

    private static void handleExpand() {
        handleExpandDo();
        handleExpandChangeState();
    }

    private static void handleExpandChangeState() {
        boolean defence = navMeshIsh2D.maps.get(NavMeshMapTypes.ENEMY_KNIGHTS)
                [getZoneCoordinate(ourQueen.position.x)][getZoneCoordinate(ourQueen.position.y)] > 10;

        if (defence) {
            gameState = GameStates.DEFEND_MODERATE;
        }
    }

    private static void handleExpandDo() {
        String order;
        Point moveToThisPoint = getMapCoordinate(navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position),
                new HashMap<NavMeshMapTypes, Integer>() {
                    {
                        put(NavMeshMapTypes.NON_FRIENDLY_PLACES_TO_BUILD, 1);
                        put(NavMeshMapTypes.BLOCKER, -1);
                    }
                }));

        Optional<Site> closestNonFriendlySite = closestNonFriendlySite(moveToThisPoint, 600);

        if (closestNonFriendlySite.isPresent()) {
            order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " " + "MINE";
        } else {
            order = "MOVE " + moveToThisPoint.x + " " + moveToThisPoint.y;
        }

        System.out.println(order);
    }

    private static Optional<Site> closestNonFriendlySite(Point moveToThisPoint, int i) {
        return sites.stream()
                .filter(site -> site.getSiteStatus().getGold() > 10)
                .filter(distanceIsBelow(moveToThisPoint, i))
                .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.ENEMY) ||
                        !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY) ||
                        (site.getSiteStatus().getStructureType().equals(StructureType.MINE) &&
                                site.getSiteStatus().getMaxMineSize() > site.getSiteStatus().getParam1()) ||
                        site.getSiteStatus().getStructureType().equals(StructureType.TOWER)
                )
                .min(distanceTo(ourQueen.position));
    }

    private static void handleStarting() {
        String order;
        Point moveToThisPoint = getMapCoordinate(navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position),
                new HashMap<NavMeshMapTypes, Integer>() {
                    {
                        put(NavMeshMapTypes.NON_FRIENDLY_PLACES_TO_BUILD, 1);
                        put(NavMeshMapTypes.BLOCKER, -1);
                    }
                }));

        Optional<Site> closestNonFriendlySite = closestNonFriendlySite(moveToThisPoint, 600);

        if (closestNonFriendlySite.isPresent()) {
            String buildType = "MINE";
            if (enemyKnightBarracks.size() > 0 && myKnightBarracks.size() < 1) {
                buildType = "BARRACKS-KNIGHT";
            }
            order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " " + buildType;
        } else {
            order = "MOVE " + moveToThisPoint.x + " " + moveToThisPoint.y;
        }

        System.out.println(order);
    }

    private static void buildBuildingsOffence() {
        boolean defence = navMeshIsh2D.maps.get(NavMeshMapTypes.ENEMY_KNIGHTS)
                [getZoneCoordinate(ourQueen.position.x)][getZoneCoordinate(ourQueen.position.y)] > 10;
        boolean strongDefence = false;

        Point moveToThisPoint;
        int radiusToBuildBuilding;
        if (defence || hideBehindFort) {
            radiusToBuildBuilding = 600;
            HashMap<NavMeshMapTypes, Integer> defenceWeights = new HashMap<NavMeshMapTypes, Integer>() {
                {
                    put(NavMeshMapTypes.ENEMY_KNIGHTS, -1);
                    put(NavMeshMapTypes.BLOCKER, -1);
                    put(NavMeshMapTypes.NON_TOWER_PLACES_TO_BUILD, 1);
                }
            };
            navMeshIsh2D.printPosition(getZoneCoordinate(ourQueen.position), 1, defenceWeights);
            Point directionAwayFromEnemyZone = navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position), defenceWeights);

            int defenceValue = navMeshIsh2D.getCostBenefit(directionAwayFromEnemyZone, defenceWeights);
            System.err.println("directionAwayFromEnemyZone: " + directionAwayFromEnemyZone);
            System.err.println("defenceValue: " + defenceValue);
            if (defenceValue > 40 && !hideBehindFort) {
                //Moderate defence
                System.err.println("Moderate defence");
                moveToThisPoint = getMapCoordinate(navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position),
                        new HashMap<NavMeshMapTypes, Integer>() {
                            {
                                put(NavMeshMapTypes.NON_FRIENDLY_PLACES_TO_BUILD, 1);
                                put(NavMeshMapTypes.ENEMY_KNIGHTS, -2);
                                put(NavMeshMapTypes.BLOCKER, -1);
                            }
                        }));
            } else {
                //Strong Defence
                System.err.println("Strong defence");

                HashMap<NavMeshMapTypes, Integer> towerDefenceWeight = new HashMap<NavMeshMapTypes, Integer>() {
                    {
                        put(NavMeshMapTypes.TOWER_PROTECTION, 1);
                    }
                };
                navMeshIsh2D.printPosition(getZoneCoordinate(ourQueen.position), 1, towerDefenceWeight);
                Point getBestTowerDefenceZone = navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position), towerDefenceWeight);

                int towerDefenceValue = navMeshIsh2D.getCostBenefit(getBestTowerDefenceZone, towerDefenceWeight);

                System.err.println("towerDefence: " + towerDefenceValue + ", " + getBestTowerDefenceZone);
                if (towerDefenceValue > 50 || hideBehindFort) {
                    //Fort is build now
                    System.err.println("fortDefence");

                    hideBehindFort = defenceValue > 40;

                    Optional<Site> closestEnemyBaracks = enemyKnightBarracks.stream()
                            .min(distanceTo(ourQueen.position));
                    Optional<Site> defensiveTower = Optional.empty();
                    if (closestEnemyBaracks.isPresent()) {
                        defensiveTower = myTowers.stream()
                                .max(distanceTo(closestEnemyBaracks.get().position));

                        if (defensiveTower.isPresent()) {
                            HashMap<NavMeshMapTypes, Integer> typeWeights = new HashMap<NavMeshMapTypes, Integer>() {
                                {
                                    put(NavMeshMapTypes.ENEMY_KNIGHTS, -2);
                                    put(NavMeshMapTypes.BLOCKER, -1);
                                    put(NavMeshMapTypes.TOWER_PROTECTION, 1);
                                }
                            };
                            navMeshIsh2D.printPosition(getZoneCoordinate(defensiveTower.get().position), 1, typeWeights);
                            getBestTowerDefenceZone = navMeshIsh2D.getBestNeighbour(getZoneCoordinate(defensiveTower.get().position), typeWeights);
                            towerDefenceValue = navMeshIsh2D.getCostBenefit(getBestTowerDefenceZone, typeWeights);
                            System.err.println("towerDefence: " + towerDefenceValue + ", " + getBestTowerDefenceZone);
                        }
                    }

                    if (!defensiveTower.isPresent()) {
                        HashMap<NavMeshMapTypes, Integer> typeWeights = new HashMap<NavMeshMapTypes, Integer>() {
                            {
                                put(NavMeshMapTypes.ENEMY_KNIGHTS, -2);
                                put(NavMeshMapTypes.BLOCKER, -1);
                                put(NavMeshMapTypes.TOWER_PROTECTION, 1);
                            }
                        };
                        navMeshIsh2D.printPosition(getZoneCoordinate(ourQueen.position), 1, typeWeights);
                        getBestTowerDefenceZone = navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position), typeWeights);
                        towerDefenceValue = navMeshIsh2D.getCostBenefit(getBestTowerDefenceZone, typeWeights);
                        System.err.println("towerDefence: " + towerDefenceValue + ", " + getBestTowerDefenceZone);
                    }

                    moveToThisPoint = getMapCoordinate(getBestTowerDefenceZone);

                } else {
                    radiusToBuildBuilding = 300;
                    strongDefence = true;
                    defenceWeights = new HashMap<NavMeshMapTypes, Integer>() {
                        {
                            put(NavMeshMapTypes.ENEMY_KNIGHTS, -1);
                            put(NavMeshMapTypes.BLOCKER, -1);
                            put(NavMeshMapTypes.NON_TOWER_PLACES_TO_BUILD, 1);
                            put(NavMeshMapTypes.TOWER_PROTECTION, 10);
                        }
                    };
                    navMeshIsh2D.printPosition(getZoneCoordinate(ourQueen.position), 1, defenceWeights);
                    getBestTowerDefenceZone = navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position), defenceWeights);
                    towerDefenceValue = navMeshIsh2D.getCostBenefit(getBestTowerDefenceZone, defenceWeights);
                    System.err.println("StrongDefence: " + towerDefenceValue + ", " + getBestTowerDefenceZone);
                    moveToThisPoint = getMapCoordinate(getBestTowerDefenceZone);
                }


            }
        } else {
            //Offence
            radiusToBuildBuilding = 600;
            moveToThisPoint = getMapCoordinate(navMeshIsh2D.getBestNeighbour(getZoneCoordinate(ourQueen.position),
                    new HashMap<NavMeshMapTypes, Integer>() {
                        {
                            put(NavMeshMapTypes.NON_FRIENDLY_PLACES_TO_BUILD, 1);
                            put(NavMeshMapTypes.BLOCKER, -1);
                        }
                    }));
        }
        System.err.println("moveToThisPoint: " + moveToThisPoint);
        System.err.println("moveToThisPoint: " + getZoneCoordinate(moveToThisPoint));


        String order = "MOVE " + moveToThisPoint.x + " " + moveToThisPoint.y;
        boolean gotOrder = hideBehindFort;

        if (hideBehindFort) {
            Optional<Site> closestFreindlyTowerSite = myTowers.stream()
                    .filter(distanceIsBelow(moveToThisPoint, radiusToBuildBuilding))
                    .filter(site -> site.getSiteStatus().getStructureType().equals(StructureType.TOWER) &&
                            site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                    .min(distanceTo(moveToThisPoint));

            if (closestFreindlyTowerSite.isPresent()) {
                order = "BUILD " + closestFreindlyTowerSite.get().getSiteId() + " TOWER";
                gotOrder = true;
            }
        }

        if (defence && !gotOrder) {
            boolean finalStrongDefence = strongDefence;
            Optional<Site> closestNonFriendlySite = sites.stream()
                    .filter(distanceIsBelow(moveToThisPoint, radiusToBuildBuilding))
                    .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.BARRACKS) ||
                            !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY))
                    .filter(site -> !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                    .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY) || finalStrongDefence)
                    .min(distanceTo(ourQueen.getPosition()));

            if (closestNonFriendlySite.isPresent()) {
                order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " TOWER";
                gotOrder = true;
            }
        }

        if (!defence && !gotOrder && myKnightBarracks.size() < 1) {
            int radiusToBuildBuildingCloseToEnemyQueen = 1000;

            Optional<Site> closestNonFriendlySite = sites.stream()
                    .filter(distanceIsBelow(moveToThisPoint, radiusToBuildBuilding))
                    .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.ENEMY) ||
                            !site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                    .filter(site -> !site.getSiteStatus().getOwner().equals(OwnerType.FRIENDLY) ||
                            site.getSiteStatus().getStructureType().equals(StructureType.TOWER))
                    .min(distanceTo(ourQueen.getPosition()));

            System.err.println("My mines count: " + myMines.size());
            if (closestNonFriendlySite.isPresent() &&
                    (distanceIsBelow(theirQueen.position, radiusToBuildBuildingCloseToEnemyQueen).test(closestNonFriendlySite.get()) ||
                            myMines.size() >= 3)) {
                order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " BARRACKS-KNIGHT";
                gotOrder = true;
            }
        }

        if (!defence && !gotOrder) {
            Optional<Site> closestNonFriendlySite = closestNonFriendlySite(moveToThisPoint, radiusToBuildBuilding);

            if (closestNonFriendlySite.isPresent()) {
                order = "BUILD " + closestNonFriendlySite.get().getSiteId() + " MINE";
                gotOrder = true;
            }
        }


        System.out.println(order);
    }

    private static void trainUnitsOffence() {
        List<Integer> goldUsed = new LinkedList<>();

        int radiusNotToSpawnFromEnemyQueen = 100;

        System.err.println("enemyTowers.size(): " + enemyTowers.size());
        List<String> knightSites = new LinkedList<>();
        if (enemyArchers.size() < 6 && enemyTowers.size() < 6 && (isBursting || GameStates.STARTING.equals(gameState))) {
            knightSites = sitesReadyToTrain.stream()
                    .filter(distanceIsAbove(theirQueen.position, radiusNotToSpawnFromEnemyQueen))
                    .filter(site -> site.getSiteStatus().getUnitType().equals(UnitType.KNIGHT))
                    .filter(canPayForTraining(gold, goldUsed))
                    .sorted(distanceTo(theirQueen.getPosition()))
                    .map(Site::getSiteId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            System.err.println("knightSites: ");

            if (!knightSites.isEmpty() && gameState.equals(GameStates.STARTING)) {
                gameState = GameStates.EXPAND;
            }
        }

        if (gold > 300) isBursting = true;
        if (gold < 100) isBursting = false;

        System.out.println(
                knightSites.stream()
                        .collect(Collectors.joining(" ", "TRAIN ", ""))
                        .trim());
    }

    private static Predicate<HasPosition> distanceIsBelow(Point point, int radiusToSpawnKnights) {
        return site -> distanceIsBelow(point, radiusToSpawnKnights, site.getPosition());
    }

    private static boolean distanceIsBelow(Point pointA, int radiusToSpawnKnights, Point pointB) {
        return radiusToSpawnKnights > Math.abs(pointA.distance(pointB));
    }

    private static Predicate<HasPosition> distanceIsAbove(Point point, int radiusNotToSpawnFromEnemyQueen) {
        return site -> radiusNotToSpawnFromEnemyQueen < Math.abs(point.distance(site.getPosition()));
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
        return (site1, site2) -> compareClosestToPoint(point, site1.getPosition(), site2.getPosition());
    }

    private static Comparator<Point> distanceToPoints(Point point) {
        return (site1, site2) -> compareClosestToPoint(point, site1, site2);
    }

    private static int compareClosestToPoint(Point mainPoint, Point site1, Point site2) {
        double site1DistanseToQueen = mainPoint.distanceSq(site1);
        double site2DistanseToQueen = mainPoint.distanceSq(site2);
        return (int) (Math.abs(site1DistanseToQueen) - Math.abs(site2DistanseToQueen));
    }
}