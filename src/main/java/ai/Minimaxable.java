package ai;

/**
 * A state that has a computable minimax value and is mutable via playing moves
 * of type {@code M}.
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
