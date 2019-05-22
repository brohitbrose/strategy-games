package stratgame.game;

/**
 * Entity responsible for playing moves in a {@link Game}.
 *
 * @param <M> the type of move that this {@code Player} plays.
 * @param <P> the type of field that uniquely identifies this player.
 */
public interface Player<M, P> {

  /**
   * Returns a move from {@code possible} after considering {@code state}.
   */
  M decide(State<M, P> state);
}
