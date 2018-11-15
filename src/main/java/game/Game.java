package game;

/**
 * Entity that manages the lifecycle of a match.
 *
 * @param <M> the {@code Move} type that can be played in this {@code Game}.
 */
public interface Game<M> {

  /**
   * The {@code Player} who is expected to move on the current turn.
   */
  Player<M> current();

  /**
   * Context used to manage the state of this {@code Game}.
   */
  State<M> state();

  /**
   * Prompts {@code current()} to make a move.
   */
  void await();

  /**
   * Starts this {@code Game}.
   */
  void start();

  /**
   * Plays {@code m} and updates {@code state()} accordingly.
   */
  default void makeMove(M m) {
    if (state().makeMove(m)) {
      state().debug();
      if (!state().isOver()) {
        await();
      }
    }
  }
}
