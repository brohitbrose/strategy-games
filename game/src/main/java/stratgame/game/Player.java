package stratgame.game;

/**
 * Entity responsible for playing moves in a {@link Game}.
 *
 * @param <M> the type of move that this {@code Player} plays.
 */
public interface Player<M> {

  /**
   * Returns a move from {@code possible} after considering {@code state}.
   */
  M decide(State<M> state);
}
