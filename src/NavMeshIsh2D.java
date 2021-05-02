import java.awt.*;

public class NavMeshIsh2D {
    int[][] costMap;
    int[][] benefitMap;
    int heightInZone;
    int widthInZones;

    public NavMeshIsh2D(int heightInZone, int widthInZones) {
        this.costMap = new int[heightInZone][widthInZones];
        this.benefitMap = new int[heightInZone][widthInZones];
        this.heightInZone = heightInZone;
        this.widthInZones = widthInZones;
    }

    public void insertGradiantValue(Point startingPoint, int cost, int costDecreasePrZone, boolean isCost) {
        if (isCost) {
            costMap[startingPoint.x][startingPoint.y] = cost;
        } else {
            benefitMap[startingPoint.x][startingPoint.y] = cost;
        }

        int count = 1;
        for (int currentCost = cost - costDecreasePrZone; currentCost > 0; currentCost -= costDecreasePrZone) {
            //Four rows

            //Top row
            int currentX = startingPoint.x + count;
            if (currentX < heightInZone) {
                for (int currentY = startingPoint.y - count; currentY <= startingPoint.y + count; currentY++) {
                    if (currentY >= 0 && currentY < widthInZones) {
                        extracted(currentCost, currentY, currentX, isCost);
                    }
                }
            }

            //Bottom row
            currentX = startingPoint.x - count;
            if (currentX >= 0) {
                for (int currentY = startingPoint.y - count; currentY <= startingPoint.y + count; currentY++) {
                    if (currentY >= 0 && currentY < widthInZones) {
                        extracted(currentCost, currentY, currentX, isCost);
                    }
                }
            }

            //Right row
            int currentY = startingPoint.y + count;
            if (currentY < widthInZones) {
                for (currentX = startingPoint.x - count + 1; currentX <= startingPoint.x + count - 1; currentX++) {
                    if (currentX >= 0 && currentX < heightInZone) {
                        extracted(currentCost, currentY, currentX, isCost);
                    }
                }
            }

            //Left row
            currentY = startingPoint.y - count;
            if (currentY >= 0) {
                for (currentX = startingPoint.x - count + 1; currentX <= startingPoint.x + count - 1; currentX++) {
                    if (currentX >= 0 && currentX < heightInZone) {
                        extracted(currentCost, currentY, currentX, isCost);
                    }
                }
            }

            count++;
        }


    }

    private void extracted(int currentCost, int currentY, int currentX, boolean isCost) {
        if (isCost) {
            int tempCost = costMap[currentX][currentY];
            costMap[currentX][currentY] = tempCost + currentCost;
        } else {
            int tempCost = benefitMap[currentX][currentY];
            benefitMap[currentX][currentY] = tempCost + currentCost;
        }
    }

    public void printMaps() {
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 15; y++) {
                System.err.print("[" + x + ":" + y + "," + costMap[x][y] + "," + benefitMap[x][y] + "]");
            }
            System.err.println();
        }
    }


}
