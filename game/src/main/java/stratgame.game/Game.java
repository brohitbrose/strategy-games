package stratgame.game;

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
   * Copy of context used to manage the state of this {@code Game}.
   */
  State<M> snapshot();

  /**
   * Prompts {@code current()} to make a move.
   */
  default void await() {
    final State<M> copy = snapshot();
    System.out.println("Possible: " + copy.validMoves());
    playMove(current().decide(copy, copy.validMoves()));
  }

  /**
   * Starts this {@code Game}.
   */
  void start();

  /**
   * Plays {@code m} and updates {@code state()} accordingly, then {@code
   * await()}s the appropriate player if the game hasn't finished.
   */
  default void playMove(M m) {
    if (state().makeMove(m)) {
      state().debug();
      if (!state().isOver()) {
        await();
      }
    }
  }
}
