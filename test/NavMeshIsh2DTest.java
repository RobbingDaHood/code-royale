import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NavMeshIsh2DTest {

    @Test
    public void simpleInitialisationTest() {
        NavMeshIsh2D navMeshIsh2D = new NavMeshIsh2D(20, 15);

        for (int y = 14; y >= 0; y--) {
            for (int x = 19; x >= 0; x--) {
                assertEquals(0, navMeshIsh2D.maps.get(NavMeshMapTypes.COST)[x][y]);
                assertEquals(0, navMeshIsh2D.maps.get(NavMeshMapTypes.BENEFIT)[x][y]);
                assertEquals(0, navMeshIsh2D.maps.get(NavMeshMapTypes.BLOCKER)[x][y]);
            }
        }
    }

//    @Test
//    public void simpleAddCost() {
//        NavMeshIsh2D navMeshIsh2D = new NavMeshIsh2D(2000, 2000);
//
//        navMeshIsh2D.insertGradiantValue(new Point(1000, 1000), 3000, 1, true);
//
//        navMeshIsh2D.printMaps();
//    }

    @Test
    public void simpleAddMix() {
        NavMeshIsh2D navMeshIsh2D = new NavMeshIsh2D(20, 15);

        navMeshIsh2D.insertGradiantValue(new Point(2, 2), 100, 1, 99, NavMeshMapTypes.COST);
        navMeshIsh2D.insertGradiantValue(new Point(2, 2), 100, 2, 99, NavMeshMapTypes.BENEFIT);

        navMeshIsh2D.printMaps();

        assertEquals(100, navMeshIsh2D.maps.get(NavMeshMapTypes.COST)[2][2]);
        assertEquals(99, navMeshIsh2D.maps.get(NavMeshMapTypes.COST)[1][1]);
        assertEquals(98, navMeshIsh2D.maps.get(NavMeshMapTypes.COST)[0][0]);
        assertEquals(99, navMeshIsh2D.maps.get(NavMeshMapTypes.COST)[3][3]);
        assertEquals(98, navMeshIsh2D.maps.get(NavMeshMapTypes.COST)[3][4]);
    }

    @Test
    public void simpleGetBestNeighbour() {
        NavMeshIsh2D navMeshIsh2D = new NavMeshIsh2D(20, 15);

        navMeshIsh2D.insertGradiantValue(new Point(2, 2), 100, 1, 99, NavMeshMapTypes.COST);
        navMeshIsh2D.insertGradiantValue(new Point(5, 6), 100, 2, 99, NavMeshMapTypes.COST);

        navMeshIsh2D.insertGradiantValue(new Point(1, 1), 100, 1, 99, NavMeshMapTypes.BENEFIT);
        navMeshIsh2D.insertGradiantValue(new Point(7, 9), 100, 2, 99, NavMeshMapTypes.BENEFIT);

        navMeshIsh2D.printMaps();

        assertEquals(new Point(2, 4), navMeshIsh2D.getBestNeighbour(new Point(3, 3),
                new HashMap<NavMeshMapTypes, Integer>() {
                    {
                        put(NavMeshMapTypes.COST, -1);
                        put(NavMeshMapTypes.BENEFIT, 1);
                    }
                }));
        assertEquals(new Point(2, 4), navMeshIsh2D.getBestNeighbour(new Point(3, 3),
                new HashMap<NavMeshMapTypes, Integer>() {
                    {
                        put(NavMeshMapTypes.COST, 0);
                        put(NavMeshMapTypes.BENEFIT, 1);
                    }
                }));
        assertEquals(new Point(4, 2), navMeshIsh2D.getBestNeighbour(new Point(3, 3),
                new HashMap<NavMeshMapTypes, Integer>() {
                    {
                        put(NavMeshMapTypes.COST, -1);
                        put(NavMeshMapTypes.BENEFIT, 0);
                    }
                }));

        assertEquals(new Point(7, 9), navMeshIsh2D.getBestNeighbour(new Point(7, 9),
                new HashMap<NavMeshMapTypes, Integer>() {
                    {
                        put(NavMeshMapTypes.COST, -1);
                        put(NavMeshMapTypes.BENEFIT, 1);
                    }
                }));
        assertEquals(new Point(7, 9), navMeshIsh2D.getBestNeighbour(new Point(7, 9),
                new HashMap<NavMeshMapTypes, Integer>() {
                    {
                        put(NavMeshMapTypes.COST, 0);
                        put(NavMeshMapTypes.BENEFIT, 1);
                    }
                }));
        assertEquals(new Point(6, 10), navMeshIsh2D.getBestNeighbour(new Point(7, 9),
                new HashMap<NavMeshMapTypes, Integer>() {
                    {
                        put(NavMeshMapTypes.COST, -1);
                        put(NavMeshMapTypes.BENEFIT, 0);
                    }
                }));
    }

    @Test
    public void simpleAddMaxRange() {
        NavMeshIsh2D navMeshIsh2D = new NavMeshIsh2D(20, 15);

        navMeshIsh2D.insertGradiantValue(new Point(2, 2), 100, 1, 2, NavMeshMapTypes.COST);

        navMeshIsh2D.printMaps();
    }

    @Test
    public void simplePrintPosition() {
        NavMeshIsh2D navMeshIsh2D = new NavMeshIsh2D(20, 15);

        navMeshIsh2D.insertGradiantValue(new Point(2, 2), 100, 1, 99, NavMeshMapTypes.COST);
        navMeshIsh2D.insertGradiantValue(new Point(2, 2), 100, 2, 99, NavMeshMapTypes.BENEFIT);

        navMeshIsh2D.printPosition(new Point(2, 2), 6);
        navMeshIsh2D.printPosition(new Point(2, 2), 2);
    }

}