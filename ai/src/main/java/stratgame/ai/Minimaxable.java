package stratgame.ai;

/**
 * Mutable state, not necessarily a {@code stratgame.game.State}, with a
 * computable minimax value.
 */
public interface Minimaxable<M> {

  /**
   * The minimax value of this state.
   */
  int minimaxValue();

  /**
   * Updates this {@code Minimaxable} by playing {@code move}.
   */
  void move(M move);

  /**
   * Returns a new {@code Minimaxable} that captures the result of playing
   * {@code m} to {@code this} (but leaves {@code this} untouched).
   */
  Minimaxable<M> cloneAndMove(M m);
}
