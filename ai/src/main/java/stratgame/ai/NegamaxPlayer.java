package stratgame.ai;

import java.util.ArrayList;
import java.util.List;
import stratgame.game.Player;
import stratgame.game.State;

/**
 * {@link Player} that uses a minimax strategy to decide plays.
 * <p>
 * Unlike with the {@code Player} interfaces in the {@code stratgame.game}
 * package, implementers of this class can choose not to override {@link
 * #decide(State)}, and instead overriding {@link #terminalValue(State)}.
 * This drastically reduces the boilerplate required for custom negamax
 * implementations in various games.
 * <p>
 *
 * @param <M> the type of move that this {@code Player} plays.
 * @param <P> the type of mark that identifies this {@code Player} in a game.
 * @param <S> the type of {@code State} to which this {@code Player} submits
 *           moves.
 */
public abstract class NegamaxPlayer<M, P, S extends State<M, P>>
    implements Player<M, P> {

  /**
   * The mark that identifies this {@code Player} in a game.
   */
  protected P piece;

  protected NegamaxPlayer() { }

  @Override
  @SuppressWarnings("unchecked")
  public M decide(State<M, P> state) {
    int bestValue = Integer.MIN_VALUE;
    M bestChoice = null;
    int alpha = -0x7FFFFFFF;
    int beta = 0x7FFFFFFF;
    final List<M> possible = new ArrayList(state.validMoves());
    for (M choice : possible) {
      state.makeMove(choice);
      final int nv = -negamax((S) state, -beta, -alpha, -1);
      if (nv > bestValue) {
        bestValue = nv;
        bestChoice = choice;
        alpha = bestValue;
      }
      state.undo();
    }
    return bestChoice;
  }

  @SuppressWarnings("unchecked")
  public int negamax(S node, int alpha, int beta, int color) {
    if (node.isOver()) {
      return color * terminalValue(node);
    }
    int bestSoFar = Integer.MIN_VALUE;
    final List<M> snapshot = new ArrayList(node.validMoves());
    for (M choice : snapshot) {
      node.makeMove(choice);
      final int newValue = -negamax(node, -beta, -alpha, -color);
      if (newValue > bestSoFar) {
        bestSoFar = newValue;
        if (bestSoFar > alpha) {
          alpha = bestSoFar;
          if (alpha >= beta) {
            node.undo();
            break;
          }
        }
      }
      node.undo();
    }
    return bestSoFar;
  }

  /**
   * Returns the negamax value of this state without evaluating any further
   * subtrees, e.g. by some heuristic or by treating {@code state} as the
   * endgame.
   */
  public abstract int terminalValue(S state);
}
