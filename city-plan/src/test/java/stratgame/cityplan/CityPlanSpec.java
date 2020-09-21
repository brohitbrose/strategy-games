package stratgame.cityplan;

import java.util.List;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class CityPlanSpec {

  @Test
  public void testHarvardB() {
    final CityPlan puzzle = buildHarvardB();
    // Neighborhood initialization
    assertEquals(puzzle.getNeighborhood(2), 0);
    assertEquals(puzzle.getNeighborhood(22), 1);
    assertEquals(puzzle.getNeighborhood(42), 2);
    assertEquals(puzzle.getNeighborhood(9), 3);
    assertEquals(puzzle.getNeighborhood(55), 4);
    assertEquals(puzzle.getNeighborhood(49), 5);
    assertEquals(puzzle.getNeighborhood(61), 6);
    assertEquals(puzzle.getNeighborhood(58), 7);
    assertEquals(puzzle.getNeighborhood(69), 8);
    // Can't exceed two in a row
    assertTrue(puzzle.makeMove(49));
    assertTrue(puzzle.makeMove(51));
    assertFalse(puzzle.makeMove(45));
    // Can exceed two in a col
    assertTrue(puzzle.makeMove(69));
    assertFalse(puzzle.makeMove(6));
    // TODO: nbh

    assertTrue(puzzle.makeMove(1));
    assertTrue(puzzle.makeMove(3));

    assertTrue(puzzle.makeMove(14));
    assertTrue(puzzle.makeMove(16));

    assertTrue(puzzle.makeMove(18));
    assertTrue(puzzle.makeMove(21));

    assertTrue(puzzle.makeMove(32));
    assertTrue(puzzle.makeMove(34));

    assertTrue(puzzle.makeMove(36));
    assertTrue(puzzle.makeMove(38));

    // assertTrue(puzzle.makeMove(49));
    // assertTrue(puzzle.makeMove(51));

    assertTrue(puzzle.makeMove(55));
    assertTrue(puzzle.makeMove(62));

    assertTrue(puzzle.makeMove(67));
    // assertTrue(puzzle.makeMove(69));

    assertTrue(puzzle.makeMove(74));
    assertFalse(puzzle.isSolved());
    assertTrue(puzzle.makeMove(80));
    assertTrue(puzzle.isSolved());

  }

  @Test
  public void solveHarvardB() {
    final CityPlan puzzle = buildHarvardB();

    final CityPlanSolver solver = new CityPlanSolver();
    assertEquals(solver.solve(puzzle),
        List.of(1, 3, 14, 16, 18, 21, 32, 34, 36, 38, 49, 51, 55, 62, 67, 69, 74, 80));
  }

  private static CityPlan buildHarvardB() {
    final int[][] neighborhoods = new int[][]{
        {0, 1, 2, 3, 4, 10, 11},
        {5, 6, 12, 13, 14, 20, 21, 22, 30, 31, 39, 40},
        {7, 8, 15, 16, 17, 23, 24, 25, 26, 32, 33, 41, 42},
        {9, 18, 27, 36},
        {19, 28, 29, 37, 38, 45, 46, 47, 48, 54, 55, 56, 63, 64, 72, 73},
        {49, 50, 51},
        {34, 35, 43, 44, 52, 53, 60, 61, 62},
        {57, 58, 59, 65, 66, 67, 68, 74, 75, 76, 77},
        {69, 70, 71, 78, 79, 80}
    };
    return new CityPlan(neighborhoods);
  }
}
