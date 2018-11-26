package stratgame.game;

import java.util.List;

/**
 * Entity responsible for playing moves in a {@link Game}.
 *
 * @param <M> the type of {@code Move} that this {@code Player} plays.
 */
public interface Player<M> {

  /**
   * Returns a move from {@code possible} after considering {@code state}.
   */
  M decide(State<M> state);
}
