import java.awt.*;

public class NavMeshIsh2D {
    int[][] costMap;
    int[][] benefitMap;
    int[][] blockerMap;
    int heightInZone;
    int widthInZones;

    public NavMeshIsh2D(int heightInZone, int widthInZones) {
        this.costMap = new int[heightInZone][widthInZones];
        this.benefitMap = new int[heightInZone][widthInZones];
        this.blockerMap = new int[heightInZone][widthInZones];
        this.heightInZone = heightInZone;
        this.widthInZones = widthInZones;
    }

    public Point getBestNeighbour(Point currentPosition, int costWeight, int benefitWeight) {
        Point result = currentPosition;

        int currentY = currentPosition.y - 1;
        if (currentY >= 0) {
            for (int currentX = currentPosition.x - 1; currentX <= currentPosition.x + 1; currentX++) {
                if (currentX >= 0 && currentX < heightInZone) {
                    result = getBestCandidate(costWeight, benefitWeight, result, currentY, currentX);
                }
            }
        }

        currentY = currentPosition.y + 1;
        if (currentY < widthInZones) {
            for (int currentX = currentPosition.x - 1; currentX <= currentPosition.x + 1; currentX++) {
                if (currentX >= 0 && currentX < heightInZone) {
                    result = getBestCandidate(costWeight, benefitWeight, result, currentY, currentX);
                }
            }
        }

        int currentX = currentPosition.x + 1;
        currentY = currentPosition.y;
        if (currentX < heightInZone) {
            result = getBestCandidate(costWeight, benefitWeight, result, currentY, currentX);
        }

        currentX = currentPosition.x - 1;
        if (currentX >= 0) {
            result = getBestCandidate(costWeight, benefitWeight, result, currentY, currentX);
        }

        return result;
    }

    private Point getBestCandidate(int costWeight, int benefitWeight, Point result, int currentY, int currentX) {
        Point candidate = new Point(currentX, currentY);
        if (getCostBenefit(result, costWeight, benefitWeight) < getCostBenefit(candidate, costWeight, benefitWeight)) {
            result = candidate;
        }
        return result;
    }

    public int getCostBenefit(Point position, int costWeight, int benefitWeight) {
        return benefitMap[position.x][position.y] * benefitWeight - blockerMap[position.x][position.y] * costWeight - costMap[position.x][position.y] * costWeight;
    }

    public void insertGradiantValue(Point startingPoint, int cost, int costDecreasePrZone, int maxRange, NavMeshMapTypes navMeshMapTypes) {
        updateValues(cost, startingPoint.y, startingPoint.x, navMeshMapTypes);

        int count = 1;
        for (int currentCost = cost - costDecreasePrZone; currentCost > 0 && count <= maxRange; currentCost -= costDecreasePrZone) {
            //Four rows

            //Top row
            int currentX = startingPoint.x + count;
            if (currentX < heightInZone) {
                for (int currentY = startingPoint.y - count; currentY <= startingPoint.y + count; currentY++) {
                    if (currentY >= 0 && currentY < widthInZones) {
                        updateValues(currentCost, currentY, currentX, navMeshMapTypes);
                    }
                }
            }

            //Bottom row
            currentX = startingPoint.x - count;
            if (currentX >= 0) {
                for (int currentY = startingPoint.y - count; currentY <= startingPoint.y + count; currentY++) {
                    if (currentY >= 0 && currentY < widthInZones) {
                        updateValues(currentCost, currentY, currentX, navMeshMapTypes);
                    }
                }
            }

            //Right row
            int currentY = startingPoint.y + count;
            if (currentY < widthInZones) {
                for (currentX = startingPoint.x - count + 1; currentX <= startingPoint.x + count - 1; currentX++) {
                    if (currentX >= 0 && currentX < heightInZone) {
                        updateValues(currentCost, currentY, currentX, navMeshMapTypes);
                    }
                }
            }

            //Left row
            currentY = startingPoint.y - count;
            if (currentY >= 0) {
                for (currentX = startingPoint.x - count + 1; currentX <= startingPoint.x + count - 1; currentX++) {
                    if (currentX >= 0 && currentX < heightInZone) {
                        updateValues(currentCost, currentY, currentX, navMeshMapTypes);
                    }
                }
            }

            count++;
        }


    }

    private void updateValues(int currentCost, int currentY, int currentX, NavMeshMapTypes navMeshMapTypes) {
        if (navMeshMapTypes.equals(NavMeshMapTypes.COST)) {
            int tempCost = costMap[currentX][currentY];
            costMap[currentX][currentY] = tempCost + currentCost;
        } else if (navMeshMapTypes.equals(NavMeshMapTypes.BENEFIT)) {
            int tempCost = benefitMap[currentX][currentY];
            benefitMap[currentX][currentY] = tempCost + currentCost;
        } else {
            int tempCost = blockerMap[currentX][currentY];
            blockerMap[currentX][currentY] = tempCost + currentCost;
        }
    }

    public void printMaps() {
        for (int y = 0; y < widthInZones; y++) {
            for (int x = 0; x < heightInZone; x++) {
                System.err.print("[" + x + ":" + y + "," + costMap[x][y] + "," + benefitMap[x][y] + "]");
            }
            System.err.println();
        }
    }

    public void printPosition(Point currentPosition, int range) {
        for (int y = currentPosition.y - range; y <= currentPosition.y + range; y++) {
            if (y < widthInZones && y >= 0) {
                for (int x = currentPosition.x - range; x <= currentPosition.x + range; x++) {
                    if (x < heightInZone && x >= 0) {
                        System.err.print("[" + x + ":" + y + "," + costMap[x][y] + "," + benefitMap[x][y] + "]");
                    }
                }
            }
            System.err.println();
        }
    }


}
