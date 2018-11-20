package stratgame.tictactoe;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class TTTStateSpec {

  @Test
  public void testLifecycle() {
    final TTTState state = new TTTState();
    // some invalid moves, exhaustive takes too long
    for (int i = 9; i > 0; i += (int) (Math.random() * 80)) {
      assertEquals(state.makeMove(i), false);
    }
    for (int i = -9; i < 0; i -= (int) (Math.random() * 80)) {
      assertEquals(state.makeMove(i), false);
    }
    assertEquals(state.makeMove(0), true);
    assertEquals(state.makeMove(0), false);
    assertEquals(state.movesMade(), 1);
    assertEquals(state.makeMove(4), true);
    assertEquals(state.makeMove(4), false);
    assertEquals(state.movesMade(), 2);
    assertEquals(state.makeMove(8), true);
    assertEquals(state.makeMove(6), true);
    assertEquals(state.makeMove(2), true);
    assertEquals(state.makeMove(1), true);
    assertEquals(state.makeMove(5), true);
    assertEquals(state.isOver(), true);
    assertEquals(state.movesMade(), 7);
    assertEquals(state.makeMove(7), false);
  }

  @Test
  public void testHorizontalWinConditions() {
    // top row
    TTTState state = new TTTState();
    state.makeMove(0); state.makeMove(3);
    state.makeMove(1); state.makeMove(4);
    state.makeMove(2);
    assertEquals(state.winner(), Piece.X);

    // middle row
    state = new TTTState();
    state.makeMove(3); state.makeMove(0);
    state.makeMove(4); state.makeMove(1);
    state.makeMove(5);
    assertEquals(state.winner(), Piece.X);

    // bottom row
    state = new TTTState();
    state.makeMove(3); state.makeMove(6);
    state.makeMove(4); state.makeMove(7);
    state.makeMove(1); state.makeMove(8);
    assertEquals(state.winner(), Piece.O);
  }

  @Test
  public void testVerticalWinConditions() {
    // top row
    TTTState state = new TTTState();
    state.makeMove(0); state.makeMove(2);
    state.makeMove(3); state.makeMove(5);
    state.makeMove(6);
    assertEquals(state.winner(), Piece.X);

    // middle row
    state = new TTTState();
    state.makeMove(1); state.makeMove(0);
    state.makeMove(4); state.makeMove(5);
    state.makeMove(7);
    assertEquals(state.winner(), Piece.X);

    // bottom row
    state = new TTTState();
    state.makeMove(0); state.makeMove(2);
    state.makeMove(3); state.makeMove(5);
    state.makeMove(4); state.makeMove(8);
    assertEquals(state.winner(), Piece.O);
  }

  @Test
  public void testDiagonalWinConditions() {
    // downward diagonal
    TTTState state = new TTTState();
    state.makeMove(0); state.makeMove(2);
    state.makeMove(4); state.makeMove(5);
    state.makeMove(8);
    assertEquals(state.winner(), Piece.X);

    // upward diagonal
    state = new TTTState();
    state.makeMove(0); state.makeMove(2);
    state.makeMove(1); state.makeMove(4);
    state.makeMove(5); state.makeMove(6);
    assertEquals(state.winner(), Piece.O);
  }
}
