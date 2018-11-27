package stratgame.ultimate;

import org.testng.annotations.Test;
import stratgame.tictactoe.Piece;
import static org.testng.Assert.assertEquals;

public class UltimateStateSpec {

  @Test
  public void testLifecycle() {
    final UltimateState state = new UltimateState();
    assertEquals(state.makeMove(UltimateState.project(2,3)), true);
    // O can only move within global index 3
    assertEquals(state.validMoves().size(), 9);
    assertEquals(state.makeMove(UltimateState.project(2,4)), false);
    assertEquals(state.makeMove(UltimateState.project(3,2)), true);
    // Can't move in already-occupied spot
    assertEquals(state.validMoves().size(), 8);
    assertEquals(state.makeMove(UltimateState.project(2,3)), false);
    assertEquals(state.makeMove(UltimateState.project(2,4)), true);
    // Confirm that valid global index gets updated
    assertEquals(state.validMoves().size(), 9);
    assertEquals(state.makeMove(UltimateState.project(2,2)), false);
    assertEquals(state.makeMove(UltimateState.project(4,2)), true);
    // Give X a local victory, forbid further plays on that board
    assertEquals(state.makeMove(UltimateState.project(2,5)), true);
    assertEquals(state.makeMove(UltimateState.project(5,2)), true);
    // 81 (total) - 9 (Individual 2 gone) - 2 ((4,2) and (5,2))
    assertEquals(state.validMoves().size(), 69);
    assertEquals(state.makeMove(UltimateState.project(2,7)), false);
    assertEquals(state.makeMove(UltimateState.project(4,5)), true);
    assertEquals(state.movesMade(), 7);
    // Give O a local victory
    assertEquals(state.makeMove(UltimateState.project(5,8)), true);
    assertEquals(state.makeMove(UltimateState.project(8,5)), true);
    assertEquals(state.makeMove(UltimateState.project(5,5)), true);
    // Give O another local victory
    assertEquals(state.validMoves().size(), 59);
    assertEquals(state.makeMove(UltimateState.project(5,6)), false);
    assertEquals(state.makeMove(UltimateState.project(6,3)), true);
    assertEquals(state.makeMove(UltimateState.project(3,5)), true);
    assertEquals(state.makeMove(UltimateState.project(6,5)), true);
    assertEquals(state.makeMove(UltimateState.project(3,8)), true);
    // Give X a local victory
    assertEquals(state.makeMove(UltimateState.project(8,7)), true);
    assertEquals(state.makeMove(UltimateState.project(7,4)), true);
    assertEquals(state.makeMove(UltimateState.project(4,3)), true);
    assertEquals(state.makeMove(UltimateState.project(1,4)), true);
    assertEquals(state.makeMove(UltimateState.project(4,4)), true);
    // Give X the win
    assertEquals(state.makeMove(UltimateState.project(0,6)), true);
    assertEquals(state.makeMove(UltimateState.project(6,4)), true);
    assertEquals(state.isOver(), true);
    assertEquals(state.winner(), Piece.X);
    assertEquals(state.validMoves().size(), 0);
  }
}
