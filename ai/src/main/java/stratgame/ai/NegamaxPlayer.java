package stratgame.ai;

import java.util.List;
import stratgame.game.Player;
import stratgame.game.State;

/**
 * {@link Player} that uses a minimax strategy to decide plays.
 */
public abstract class NegamaxPlayer<M,
                                    P,
                                    S extends State<M>>
    implements Player<M> {

  /**
   * The mark that identifies this {@code Player} in a game.
   */
  protected P piece;

  public NegamaxPlayer() { }

  /**
   * Returns the {@code NegamaxView} that is the result of placing {@code piece}
   * in {@code trueState} at position {@code choice}, assuming that the current
   * negamax bounds are {@code alpha} and {@code beta}.
   */
  protected abstract NegamaxView<M, P, S> candidate(State<M> trueState, P piece, int alpha, int beta, M choice);

  @Override
  public M decide(State<M> trueState) {
    int bestValue = Integer.MIN_VALUE;
    M bestChoice = null;
    int alpha = -100;
    int beta = 100;
    final List<M> possible = trueState.validMoves();
    for (M choice : possible) {
      final NegamaxView<M, P, S> updatedState = candidate(trueState, this.piece, alpha, beta, choice);
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
