import java.awt.*;
import java.util.Arrays;
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
        Point result = currentPosition;

        int currentY = currentPosition.y - 1;
        if (currentY >= 0) {
            for (int currentX = currentPosition.x - 1; currentX <= currentPosition.x + 1; currentX++) {
                if (currentX >= 0 && currentX < heightInZone) {
                    result = getBestCandidate(typeWeights, result, new Point(currentX, currentY));
                }
            }
        }

        currentY = currentPosition.y + 1;
        if (currentY < widthInZones) {
            for (int currentX = currentPosition.x - 1; currentX <= currentPosition.x + 1; currentX++) {
                if (currentX >= 0 && currentX < heightInZone) {
                    result = getBestCandidate(typeWeights, result, new Point(currentX, currentY));
                }
            }
        }

        int currentX = currentPosition.x + 1;
        currentY = currentPosition.y;
        if (currentX < heightInZone) {
            result = getBestCandidate(typeWeights, result, new Point(currentX, currentY));
        }

        currentX = currentPosition.x - 1;
        if (currentX >= 0) {
            result = getBestCandidate(typeWeights, result, new Point(currentX, currentY));
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
        int[][] map = maps.get(navMeshMapTypes);
        updateValues(cost, startingPoint.y, startingPoint.x, map);

        int count = 1;
        for (int currentCost = cost - costDecreasePrZone; currentCost > 0 && count <= maxRange; currentCost -= costDecreasePrZone) {
            //Four rows

            //Top row
            int currentX = startingPoint.x + count;
            if (currentX < heightInZone) {
                for (int currentY = startingPoint.y - count; currentY <= startingPoint.y + count; currentY++) {
                    if (currentY >= 0 && currentY < widthInZones) {
                        updateValues(currentCost, currentY, currentX, map);
                    }
                }
            }

            //Bottom row
            currentX = startingPoint.x - count;
            if (currentX >= 0) {
                for (int currentY = startingPoint.y - count; currentY <= startingPoint.y + count; currentY++) {
                    if (currentY >= 0 && currentY < widthInZones) {
                        updateValues(currentCost, currentY, currentX, map);
                    }
                }
            }

            //Right row
            int currentY = startingPoint.y + count;
            if (currentY < widthInZones) {
                for (currentX = startingPoint.x - count + 1; currentX <= startingPoint.x + count - 1; currentX++) {
                    if (currentX >= 0 && currentX < heightInZone) {
                        updateValues(currentCost, currentY, currentX, map);
                    }
                }
            }

            //Left row
            currentY = startingPoint.y - count;
            if (currentY >= 0) {
                for (currentX = startingPoint.x - count + 1; currentX <= startingPoint.x + count - 1; currentX++) {
                    if (currentX >= 0 && currentX < heightInZone) {
                        updateValues(currentCost, currentY, currentX, map);
                    }
                }
            }

            count++;
        }

    }

    private void updateValues(int currentCost, int currentY, int currentX, int[][] map) {
        int tempCost = map[currentX][currentY];
        map[currentX][currentY] = tempCost + currentCost;
    }

    public void printMaps() {
        for (int y = 0; y < widthInZones; y++) {
            for (int x = 0; x < heightInZone; x++) {
                System.err.print(printZone(x, y));
            }
            System.err.println();
        }
    }

    public void printPosition(Point currentPosition, int range) {
        for (int y = currentPosition.y - range; y <= currentPosition.y + range; y++) {
            if (y < widthInZones && y >= 0) {
                for (int x = currentPosition.x - range; x <= currentPosition.x + range; x++) {
                    if (x < heightInZone && x >= 0) {
                        System.err.print(printZone(x, y));
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

    private String printZone(int finalX, int finalY) {
        return Arrays.stream(NavMeshMapTypes.values()).sequential()
                .map(type -> type + ":" + maps.get(type)[finalX][finalY])
                .collect(Collectors.joining(",", "[" + finalX + ":" + finalY + ", ", "]"))
                .trim();
    }


}
