package stratgame.niya;

import stratgame.ai.NegamaxPlayer;
import stratgame.ai.NegamaxView;
import stratgame.ai.Negamaxable;
import stratgame.game.State;

/**
 * {@link NiyaPlayer} that uses a minimax strategy to decide plays.
 */
public class SmartPlayer extends NegamaxPlayer<NiyaMove, Color, NiyaState, View> {

  /**
   * Constructs a new {@code SmartPlayer} with color {@code color}.
   */
  public SmartPlayer(Color color) {
    super(color);
    if (color == Color.NONE) {
      throw new IllegalArgumentException("Player color cannot be NONE");
    }
  }

  @Override
  public View candidate(State<NiyaMove> trueState, Color piece, int alpha, int beta, NiyaMove choice) {
    return new View(trueState, piece, alpha, beta, choice);
  }
}

/**
 * {@link Negamaxable} extension of {@link NiyaState}.
 * <p>
 * Although some versions of Niya do not consider empty tiles remaining at the
 * end of a match, we want the AI to win in as few moves as possible.  Thus, the
 * the value of a board for the player who provided the last possible move in a
 * match, with {@code n} being the number of empty spots remaining, is:
 * <p><ol>
 *   <li> {@code 0}, if the game ended in a draw
 *   <li> {@code n+1}, if the last move resulted in a win.
 * </ol><p>
 * Because Niya can be safely treated as a zero-sum game, the board value for
 * one player is simply the negation of the value for the other.
 */
class View extends NegamaxView<NiyaMove, Color, NiyaState> {

  View(NiyaState trueState, Color p, int alpha, int beta, NiyaMove choice) {
    super(trueState, p, alpha, beta, choice);
  }

  View(State<NiyaMove> trueState, Color p, int alpha, int beta, NiyaMove choice) {
    this((NiyaState) trueState, p, alpha, beta, choice);
  }

  View(View view, NiyaMove choice) {
    super(view, choice);
  }

  @Override
  public View cloneAndMove(NiyaMove m) {
    return new View(this, m);
  }

  @Override
  public int terminalValue() {
    final int val = 16 - state.movesMade() + 1;
    return state.winner() == Color.NONE ? 0 :
        state.winner() == color ?
            val : -val;
  }
}
