package stratgame.connectfour;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class SmartPlayerSpec {

  @Test
  public void testOneMoveLeft() {
    final C4State state = new C4State();
    state.makeMove(3); state.makeMove(3);
    state.makeMove(4); state.makeMove(4);
    state.makeMove(2); state.makeMove(2);

    assertEquals(new SmartPlayer(Color.RED).negamax(state.clone(), -100, 100, 1), 36);
    assertEquals(new SmartPlayer(Color.BLUE).negamax(state.clone(), -100, 100, 1), -36);
  }

  @Test
  public void testTwoMovesLeft() {
    final C4State state = new C4State();
    state.makeMove(0); state.makeMove(4);
    state.makeMove(1); state.makeMove(1);
    state.makeMove(3); state.makeMove(3);
    state.makeMove(4); state.makeMove(4);
    state.makeMove(3); state.makeMove(3);
    state.makeMove(4);

    // A Blue SmartPlayer presented this state can lose after two moves no
    // matter what it submits this turn.
    // Total moves played = 13, yielding a score of -(42 - 13 + 1) = -30
    assertEquals(new SmartPlayer(Color.BLUE).negamax(state.clone(), -100, 100, 1), -30);

    // A Red SmartPlayer presented this state can win after two moves no
    // matter what Blue submits this turn.
    // Total moves played = 13, yielding a score of -(42 - 13 + 1) = -30
    assertEquals(new SmartPlayer(Color.RED).negamax(state.clone(), -100, 100, 1), 30);
  }
}
