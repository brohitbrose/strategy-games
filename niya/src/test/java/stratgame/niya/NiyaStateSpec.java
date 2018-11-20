package stratgame.niya;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class NiyaStateSpec {

  private static NiyaMove nm(int r, int c) {
    return new NiyaMove(r, c);
  }

  @Test
  public void testHorizontalWinConditions() {
    NiyaState state;

    // first row

    // second row
    state = new NiyaState(new int[]{
        7, 1, 15, 5,
        13, 2, 9, 12,
        0, 4, 8, 10,
        11, 14, 3, 6
      });
    state.makeMove(nm(0,1)); state.makeMove(nm(1,1));
    state.makeMove(nm(3,1)); state.makeMove(nm(1,0));
    state.makeMove(nm(0,3)); state.makeMove(nm(1,2));
    state.makeMove(nm(2,2)); state.makeMove(nm(1,3));
    assertEquals(state.winner(), Color.BLACK);

    // third row

    // fourth row
  }

  @Test
  public void testVerticalWinConditions() {
    NiyaState state;

    // first column
    state = new NiyaState(new int[]{
        7, 1, 15, 5,
        13, 2, 9, 12,
        0, 4, 8, 10,
        11, 14, 3, 6
      });
    state.makeMove(nm(0,3)); state.makeMove(nm(1,0));
    state.makeMove(nm(1,2)); state.makeMove(nm(3,0));
    state.makeMove(nm(2,2)); state.makeMove(nm(2,0));
    state.makeMove(nm(3,2)); state.makeMove(nm(0,0));
    assertEquals(state.winner(), Color.BLACK);

    // second column

    // third column

    // fourth column
  }


  @Test
  public void testDiagonalWinConditions() {
    NiyaState state;

    // downward
    state = new NiyaState(new int[]{
        7, 1, 15, 5,
        13, 2, 9, 12,
        0, 4, 8, 10,
        11, 14, 3, 6
      });
    state.makeMove(nm(0,2)); state.makeMove(nm(0,0));
    state.makeMove(nm(2,1)); state.makeMove(nm(2,2));
    state.makeMove(nm(1,3)); state.makeMove(nm(1,0));
    state.makeMove(nm(1,2)); state.makeMove(nm(3,0));
    state.makeMove(nm(2,3)); state.makeMove(nm(1,1));
    state.makeMove(nm(3,1)); state.makeMove(nm(3,3));
    assertEquals(state.winner(), Color.BLACK);

    // upward
    state = new NiyaState(new int[]{
        12, 2, 13, 8,
        7, 5, 6, 1,
        15, 3, 11, 4,
        14, 10, 9, 0
      });
    state.makeMove(nm(2,3)); state.makeMove(nm(1,2));
    state.makeMove(nm(1,0)); state.makeMove(nm(1,1));
    state.makeMove(nm(0,2)); state.makeMove(nm(3,0));
    state.makeMove(nm(2,0)); state.makeMove(nm(2,1));
    state.makeMove(nm(2,2)); state.makeMove(nm(0,3));
    assertEquals(state.winner(), Color.BLACK);

  }

  @Test
  public void testSquareWinConditions() {
    NiyaState state;

    // top-left
    state = new NiyaState(new int[]{
        7, 10, 14, 5,
        0, 13, 12, 6,
        15, 4, 3, 2,
        1, 8, 9, 11
      });
    state.makeMove(nm(2,0)); state.makeMove(nm(1,1));
    state.makeMove(nm(3,0)); state.makeMove(nm(1,0));
    state.makeMove(nm(2,2)); state.makeMove(nm(0,0));
    state.makeMove(nm(1,3)); state.makeMove(nm(0,1));
    state.debug();
    assertEquals(state.winner(), Color.BLACK);

    // top-center

    // top-right
    state = new NiyaState(new int[]{
        9, 3, 2, 15,
        12, 10, 4, 11,
        6, 7, 1, 8,
        5, 13, 0, 14
      });
    state.makeMove(nm(1,0)); state.makeMove(nm(1,2));
    state.makeMove(nm(2,1)); state.makeMove(nm(0,3));
    state.makeMove(nm(3,3)); state.makeMove(nm(0,2));
    state.makeMove(nm(0,1)); state.makeMove(nm(1,3));
    state.debug();
    assertEquals(state.winner(), Color.BLACK);

    // middle-left
    state = new NiyaState(new int[]{
        7, 2, 14, 9,
        5, 3, 6, 13,
        0, 15, 8, 10,
        1, 4, 12, 11
      });
    state.makeMove(nm(3,2)); state.makeMove(nm(2,1));
    state.makeMove(nm(1,3)); state.makeMove(nm(0,3));
    state.makeMove(nm(3,0)); state.makeMove(nm(1,1));
    state.makeMove(nm(0,0)); state.makeMove(nm(1,0));
    state.makeMove(nm(3,1)); state.makeMove(nm(2,0));
    state.debug();
    assertEquals(state.winner(), Color.BLACK);

    // middle-center
    state = new NiyaState(new int[]{
        2, 3, 8, 11,
        6, 14, 10, 13,
        5, 1, 7, 4,
        0, 12, 15, 9
      });
    state.makeMove(nm(3,3)); state.makeMove(nm(2,1));
    state.makeMove(nm(1,3)); state.makeMove(nm(1,1));
    state.makeMove(nm(3,2)); state.makeMove(nm(2,2));
    state.makeMove(nm(0,3)); state.makeMove(nm(1,2));
    state.debug();
    assertEquals(state.winner(), Color.BLACK);

    // middle-right

    // bottom-left

    // bottom-center
    state = new NiyaState(new int[]{
        0, 13, 14, 6,
        1, 2, 15, 9,
        5, 3, 8, 7,
        11, 10, 4, 12
      });
    state.makeMove(nm(1,3)); state.makeMove(nm(2,2));
    state.makeMove(nm(0,0)); state.makeMove(nm(2,1));
    state.makeMove(nm(2,3)); state.makeMove(nm(3,2));
    state.makeMove(nm(0,3)); state.makeMove(nm(3,1));
    state.debug();
    assertEquals(state.winner(), Color.BLACK);

    // bottom-right
    state = new NiyaState(new int[]{
        7, 1, 15, 5,
        13, 2, 9, 12,
        0, 4, 8, 10,
        11, 14, 3, 6
    });
    state.makeMove(nm(0,0)); state.makeMove(nm(3,3));
    state.makeMove(nm(2,1)); state.makeMove(nm(2,2));
    state.makeMove(nm(2,0)); state.makeMove(nm(3,2));
    state.makeMove(nm(3,0)); state.makeMove(nm(2,3));
    state.debug();
    assertEquals(state.winner(), Color.BLACK);
  }

  @Test
  public void testCheckmateWinCondition() {
    final NiyaState state = new NiyaState(new int[]{
        7, 1, 15, 5,
        13, 2, 9, 12,
        0, 4, 8, 10,
        11, 14, 3, 6
      });
    // MAPLE+BIRD, MAPLE+RAIN, MAPLE+SUN, IRIS+SUN, IRIS+BIRD,
    // CHERRY+BIRD, CHERRY+PAPER, PINE+PAPER, IRIS+PAPER, MAPLE+PAPER
    state.makeMove(nm(0,1)); state.makeMove(nm(1, 1));
    // Four MAPLES and four PAPERS taken -> no moves remain
    state.makeMove(nm(2,0)); state.makeMove(nm(1,3));
    state.makeMove(nm(1,0)); state.makeMove(nm(0,3));
    state.makeMove(nm(0,0)); state.makeMove(nm(3,0));
    state.makeMove(nm(0,2)); state.makeMove(nm(3,2));
    assertEquals(state.winner(), Color.BLACK);
  }
}
