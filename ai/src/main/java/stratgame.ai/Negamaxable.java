package stratgame.ai;

import stratgame.game.State;

/**
 * {@link State} with a computable negamax value.
 */
public interface Negamaxable<M> extends Minimaxable<M>, State<M> {

  /**
   * The lower bound of possible negamax values that this {@code Negamaxable}
   * can take on.
   */
  int alpha();

  /**
   * Assigns {@code alpha} to {@link #alpha()}.
   */
  void alpha(int alpha);

  /**
   * The upper bound of possible negamax values that this {@code Negamaxable}
   * can take on.
   */
  int beta();

  /**
   * Assigns {@code beta} to {@link #beta()}.
   */
  void beta(int beta);

  /**
   * Returns a new {@code Negamaxable} that captures the result of playing
   * {@code m} to {@code this} (but leaves {@code this} untouched).
   */
  Negamaxable<M> cloneAndMove(M m);

  /**
   * Returns the negamax value of this state without evaluating any further
   * subtrees, e.g. by some heuristic or by treating this state as the endgame.
   */
  int terminalValue();

  /**
   * The negamax value of this {@link State}, with a default alpha-beta pruning
   * implementation.
   */
  default int negamaxValue(int color) {
    if (this.isOver()) {
      return color * terminalValue();
    }
    int bestSoFar = Integer.MIN_VALUE;
    for (M choice : this.validMoves()) {
      final Negamaxable<M> updatedState = cloneAndMove(choice);
      final int newValue = -updatedState.negamaxValue(-color);
      if (newValue > bestSoFar) {
        bestSoFar = newValue;
        if (bestSoFar > alpha()) {
          alpha(bestSoFar);
          if (alpha() >= beta()) break;
        }
      }
    }
    return bestSoFar;
  }

  @Override
  default int minimaxValue() {
    return negamaxValue(-1);
  }
}
