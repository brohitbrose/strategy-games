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
    int alpha = -100;
    int beta = 100;
    for (NiyaMove choice : possible) {
      final View updatedState = new View(trueState, this.color, alpha, beta, choice);
      final int nv = -updatedState.minimaxValue();
      if (nv > bestValue) {
        bestValue = nv;
        bestChoice = choice;
        alpha = bestValue;
        // Note that alpha will never match or exceed beta from the top level,
        // so we skip any fast-fail checks here.
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
  private int alpha;
  private int beta;

  /**
   * Constructs a new {@code View} that is the result of playing {@code m} in
   * {@code state}, assuming that {@code state} was bound by {@code alpha} and
   * {@code beta}, and updates {@code this.alpha} and {@code this.beta}
   * accordingly.
   */
  View(NiyaState state, Color color, int alpha, int beta, NiyaMove m) {
    super(state);
    this.color = color;
    this.alpha = -beta;
    this.beta = -alpha;
    makeMove(m);
  }

  /**
   * Constructs a new {@code View} that is the result of playing {@code m} in
   * {@code view}, updating {@code alpha} and {@code beta} accordingly.
   */
  View(View view, NiyaMove m) {
    super(view);
    this.color = view.color;
    this.alpha = -view.beta;
    this.beta = -view.alpha;
    makeMove(m);
  }

  private int terminalValue() {
    final int val = 16 - this.movesMade() + 1;
    return this.winner() == Color.NONE ? 0 :
      this.winner() == color ?
        val : -val;
  }

  private int negamaxValue(int color) {
    if (this.isOver()) {
      return color * terminalValue();
    }
    int bestSoFar = Integer.MIN_VALUE;
    for (NiyaMove choice: this.validMoves()) {
      final View updatedState = new View(this, choice);
      final int newValue = -updatedState.negamaxValue(-color);
      if (newValue > bestSoFar) {
        bestSoFar = newValue;
        if (bestSoFar > alpha) {
          alpha = bestSoFar;
          if (alpha >= beta) break;
        }
      }
    }
    return bestSoFar;
  }

  @Override
  public int minimaxValue() {
    return negamaxValue(-1);
  }

  @Override
  public void move(NiyaMove move) {
    makeMove(move);
  }
}