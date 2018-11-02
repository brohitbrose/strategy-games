package ai;

/**
 * Mutable state (but not necessarily a {@link game.State}) with a computable
 * minimax value.
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
}
