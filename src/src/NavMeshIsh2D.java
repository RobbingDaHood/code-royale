package src;

import java.awt.*;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class NavMeshIsh2D {
    Map<NavMeshMapTypes, int[][]> maps = new HashMap<>();
    int heightInZone;
    int widthInZones;

    public NavMeshIsh2D(int heightInZone, int widthInZones) {
        Arrays.stream(NavMeshMapTypes.values()).sequential()
                .forEach(type -> maps.put(type, new int[heightInZone][widthInZones]));
        this.heightInZone = heightInZone;
        this.widthInZones = widthInZones;
    }

    public Point getBestNeighbour(Point currentPosition, Map<NavMeshMapTypes, Integer> typeWeights) {
        return getBest(currentPosition,typeWeights, 1);
    }

    public Point getBest(Point currentPosition, Map<NavMeshMapTypes, Integer> typeWeights, int maxRange) {
        Point result = currentPosition;

        for (int count = 1; count <= maxRange; count++) {
            //Top row
            int currentX = currentPosition.x + count;
            if (currentX < heightInZone) {
                for (int currentY = currentPosition.y - count; currentY <= currentPosition.y + count; currentY++) {
                    if (currentY >= 0 && currentY < widthInZones) {
                        result = getBestCandidate(typeWeights, result, new Point(currentX, currentY));
                    }
                }
            }

            //Bottom row
            currentX = currentPosition.x - count;
            if (currentX >= 0) {
                for (int currentY = currentPosition.y - count; currentY <= currentPosition.y + count; currentY++) {
                    if (currentY >= 0 && currentY < widthInZones) {
                        result = getBestCandidate(typeWeights, result, new Point(currentX, currentY));
                    }
                }
            }

            //Right row
            int currentY = currentPosition.y + count;
            if (currentY < widthInZones) {
                for (currentX = currentPosition.x - count + 1; currentX <= currentPosition.x + count - 1; currentX++) {
                    if (currentX >= 0 && currentX < heightInZone) {
                        result = getBestCandidate(typeWeights, result, new Point(currentX, currentY));
                    }
                }
            }

            //Left row
            currentY = currentPosition.y - count;
            if (currentY >= 0) {
                for (currentX = currentPosition.x - count + 1; currentX <= currentPosition.x + count - 1; currentX++) {
                    if (currentX >= 0 && currentX < heightInZone) {
                        result = getBestCandidate(typeWeights, result, new Point(currentX, currentY));
                    }
                }
            }
        }

        return result;
    }

    private Point getBestCandidate(Map<NavMeshMapTypes, Integer> typeWeights, Point result, Point candidate) {
        if (getCostBenefit(result, typeWeights) < getCostBenefit(candidate, typeWeights)) {
            result = candidate;
        }
        return result;
    }

    public int getCostBenefit(Point position, Map<NavMeshMapTypes, Integer> typeWeights) {
        return typeWeights.entrySet().stream()
                .map(entryset -> maps.get(entryset.getKey())[position.x][position.y] * entryset.getValue())
                .reduce(0, Integer::sum);
    }
    public void insertGradiantValue(Point startingPoint, int cost, int costDecreasePrZone, int maxRange, NavMeshMapTypes navMeshMapTypes) {
        insertValue(startingPoint, cost, costDecreasePrZone, maxRange, navMeshMapTypes, true);
    }

    public void insertValue(Point startingPoint, int cost, int costDecreasePrZone, int maxRange, NavMeshMapTypes navMeshMapTypes, boolean shouldSum) {
        int[][] map = maps.get(navMeshMapTypes);
        if (shouldSum) {
            sumValues(cost, startingPoint.y, startingPoint.x, map);
        } else {
            maxValues(cost, startingPoint.y, startingPoint.x, map);
        }

        int count = 1;
        for (int currentCost = cost - costDecreasePrZone; currentCost > 0 && count <= maxRange; currentCost -= costDecreasePrZone) {
            //Four rows

            //Top row
            int currentX = startingPoint.x + count;
            if (currentX < heightInZone) {
                for (int currentY = startingPoint.y - count; currentY <= startingPoint.y + count; currentY++) {
                    if (currentY >= 0 && currentY < widthInZones) {
                        if (shouldSum) {
                            sumValues(currentCost, currentY, currentX, map);
                        } else {
                            maxValues(currentCost, currentY, currentX, map);
                        }
                    }
                }
            }

            //Bottom row
            currentX = startingPoint.x - count;
            if (currentX >= 0) {
                for (int currentY = startingPoint.y - count; currentY <= startingPoint.y + count; currentY++) {
                    if (currentY >= 0 && currentY < widthInZones) {
                        if (shouldSum) {
                            sumValues(currentCost, currentY, currentX, map);
                        } else {
                            maxValues(currentCost, currentY, currentX, map);
                        }
                    }
                }
            }

            //Right row
            int currentY = startingPoint.y + count;
            if (currentY < widthInZones) {
                for (currentX = startingPoint.x - count + 1; currentX <= startingPoint.x + count - 1; currentX++) {
                    if (currentX >= 0 && currentX < heightInZone) {
                        if (shouldSum) {
                            sumValues(currentCost, currentY, currentX, map);
                        } else {
                            maxValues(currentCost, currentY, currentX, map);
                        }
                    }
                }
            }

            //Left row
            currentY = startingPoint.y - count;
            if (currentY >= 0) {
                for (currentX = startingPoint.x - count + 1; currentX <= startingPoint.x + count - 1; currentX++) {
                    if (currentX >= 0 && currentX < heightInZone) {
                        if (shouldSum) {
                            sumValues(currentCost, currentY, currentX, map);
                        } else {
                            maxValues(currentCost, currentY, currentX, map);
                        }
                    }
                }
            }

            count++;
        }

    }

    private void sumValues(int currentCost, int currentY, int currentX, int[][] map) {
        int tempCost = map[currentX][currentY];
        map[currentX][currentY] = tempCost + currentCost;
    }

    private void maxValues(int currentCost, int currentY, int currentX, int[][] map) {
        int tempCost = map[currentX][currentY];
        map[currentX][currentY] = Math.max(tempCost, currentCost);
    }

    public void printMaps(EnumSet<NavMeshMapTypes> types) {
        for (int y = 0; y < widthInZones; y++) {
            for (int x = 0; x < heightInZone; x++) {
                System.err.print(printZone(x, y, types));
            }
            System.err.println();
        }
    }

    public void printPosition(Point currentPosition, int range, EnumSet<NavMeshMapTypes> types) {
        for (int y = currentPosition.y - range; y <= currentPosition.y + range; y++) {
            if (y < widthInZones && y >= 0) {
                for (int x = currentPosition.x - range; x <= currentPosition.x + range; x++) {
                    if (x < heightInZone && x >= 0) {
                        System.err.print(printZone(x, y, types));
                    }
                }
            }
            System.err.println();
        }
    }

    public void printPosition(Point currentPosition, int range, Map<NavMeshMapTypes, Integer> typeWeights) {
        System.err.println("printPosition: " + typeWeights.entrySet());
        for (int y = currentPosition.y - range; y <= currentPosition.y + range; y++) {
            if (y < widthInZones && y >= 0) {
                for (int x = currentPosition.x - range; x <= currentPosition.x + range; x++) {
                    if (x < heightInZone && x >= 0) {
                        System.err.print("[" + x + ":" + y + ", " + getCostBenefit(new Point(x, y), typeWeights) + "]");
                    }
                }
            }
            System.err.println();
        }
    }

    private String printZone(int finalX, int finalY, EnumSet<NavMeshMapTypes> types) {
        return types.stream()
                .map(type -> type + ":" + maps.get(type)[finalX][finalY])
                .collect(Collectors.joining(",", "[" + finalX + ":" + finalY + ", ", "]"))
                .trim();
    }


}
