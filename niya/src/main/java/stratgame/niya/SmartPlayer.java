package stratgame.niya;

import stratgame.ai.NegamaxPlayer;

/**
 * {@link NegamaxPlayer} for {@code NiyaStates}.
 *
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
public class SmartPlayer extends NegamaxPlayer<NiyaMove, Color, NiyaState> {

  /**
   * Constructs a new {@code SmartPlayer} with color {@code color}.
   */
  public SmartPlayer(Color color) {
    if (color == Color.NONE) {
      throw new IllegalArgumentException("Player color cannot be NONE");
    }
    this.piece = color;
  }

  @Override
  public int terminalValue(NiyaState s) {
    final int val = 16 - s.movesMade() + 1;
    return s.winner() == Color.NONE ? 0 :
        s.winner() == piece ?
            val : -val;
  }
}
