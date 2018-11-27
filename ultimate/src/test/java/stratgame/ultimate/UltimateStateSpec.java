package stratgame.ultimate;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class UltimateStateSpec {

  @Test
  public void testLifecycle() {
    final UltimateState state = new UltimateState();
    state.debug();
    assertEquals(state.makeMove(UltimateState.project(2,3)), true);
    state.debug();
    // O can only move within global index 3
    assertEquals(state.makeMove(UltimateState.project(2,4)), false);
    assertEquals(state.makeMove(UltimateState.project(3,2)), true);
    state.debug();
    // Can't move in already-occupied spot
    assertEquals(state.makeMove(UltimateState.project(2,3)), false);
    assertEquals(state.makeMove(UltimateState.project(2,4)), true);
    state.debug();    
    // Confirm that valid global index gets updated
    assertEquals(state.makeMove(UltimateState.project(2,2)), false);
    assertEquals(state.makeMove(UltimateState.project(4,2)), true);
    state.debug();
    // After a local match is won, can't move to it, but anywhere else is okay
    assertEquals(state.makeMove(UltimateState.project(2,5)), true);
    state.debug();
    assertEquals(state.makeMove(UltimateState.project(5,2)), true);
    assertEquals(state.makeMove(UltimateState.project(2,7)), false);
    assertEquals(state.makeMove(UltimateState.project(4,5)), true);
    assertEquals(state.movesMade(), 7);
    state.debug();
  }
}
