package game.niya;

import ai.Minimaxable;
import java.util.List;

/**
 * {@link NiyaPlayer} that uses a minimax strategy to decide plays.
 */
public class SmartPlayer implements NiyaPlayer {

  /**
   * The {@link Color} of this {@code Player}, either {@code RED} or {@code
   * BLACK}.
   */
  private final Color color;

  /**
   * Constructs a new {@code SmartPlayer} with color {@code color}.
   */
  public SmartPlayer(Color color) {
    if (color == Color.NONE) throw new IllegalArgumentException();
    this.color = color;
  }

  @Override
  public NiyaMove decide(NiyaState trueState, List<NiyaMove> possible) {
    int bestValue = Integer.MIN_VALUE;
    NiyaMove bestChoice = null;
    // Could prune here, too. May tackle in future.
    for (NiyaMove choice: possible) {
      final View updatedState = new View(trueState, this.color);
      updatedState.move(choice);
      final int nv = -updatedState.minimaxValue();
      if (nv > bestValue) {
        bestValue = nv;
        bestChoice = choice;
      }
    }
    return bestChoice;
  }
}

/**
 * {@link Minimaxable} extension of {@link NiyaState}.
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
final class View extends NiyaState implements Minimaxable<NiyaMove> {

  private final Color color;

  View(NiyaState state, Color color) {
    super(state);
    this.color = color;
  }

  View (View view) {
    super(view);
    this.color = view.color;
  }

  private int terminalValue() {
    final int val = 16 - this.movesMade() + 1;
    return this.winner() == Color.NONE ? 0 :
      this.winner() == color ?
        val : -val;
  }

  private int negamaxValue(int color, int alpha, int beta) {
    if (this.isOver()) {
      return color * terminalValue();
    }
    int bestSoFar = Integer.MIN_VALUE;
    for (NiyaMove choice: this.validMoves()) {
      final View updatedState = new View(this);
      updatedState.move(choice);
      // Pruning proves crucial, changing the computation time for the first
      // move in a game from around 90 seconds to 1.5 seconds.
      bestSoFar = Math.max(-updatedState.negamaxValue(-color, -beta, -alpha), bestSoFar);
      alpha = Math.max(alpha, bestSoFar);
      if (alpha >= beta) break;
    }
    return bestSoFar;
  }

  @Override
  public int minimaxValue() {
    final int alpha = -100;
    final int beta = 100;
    return negamaxValue(-1, -beta, -alpha);
  }

  @Override
  public void move(NiyaMove move) {
    makeMove(move);
  }
}
