package stratgame.ai;

import stratgame.game.State;

/**
 * {@link State} with a computable negamax value.
 */
public interface Negamaxable<M> extends Minimaxable<M> {

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
  int negamaxValue(int color);

  @Override
  default int minimaxValue() {
    return negamaxValue(-1);
  }
}
