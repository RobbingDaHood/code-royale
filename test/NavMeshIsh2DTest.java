import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NavMeshIsh2DTest {

    @Test
    public void simpleInitialisationTest() {
        NavMeshIsh2D navMeshIsh2D = new NavMeshIsh2D(20, 15);

        for (int y = 14; y >= 0; y--) {
            for (int x = 19; x >= 0; x--) {
                assertEquals(0, navMeshIsh2D.costMap[x][y]);
                assertEquals(0, navMeshIsh2D.benefitMap[x][y]);
            }
        }
    }

    @Test
    public void simpleAddCost() {
        NavMeshIsh2D navMeshIsh2D = new NavMeshIsh2D(20, 15);

        navMeshIsh2D.insertGradiantCost(new Point(2, 2), 100, 1);

        navMeshIsh2D.printMaps();

        assertEquals(100, navMeshIsh2D.costMap[2][2]);
        assertEquals(99, navMeshIsh2D.costMap[1][1]);
        assertEquals(98, navMeshIsh2D.costMap[0][0]);
        assertEquals(99, navMeshIsh2D.costMap[3][3]);
        assertEquals(98, navMeshIsh2D.costMap[3][4]);
    }

}