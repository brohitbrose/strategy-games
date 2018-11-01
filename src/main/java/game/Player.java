package game;

import java.util.List;

/**
 * An entity responsible for playing moves in a {@link Game}.
 *
 * @param <S> the {@link State} type in the {@code Game} that this
 *           {@code Player} is part of.
 * @param <M> the type of {@code Move} that this {@code Player} plays.
 */
public interface Player<S extends State<M>, M> {

  /**
   * Returns a move from {@code possible} after considering {@code state}.
   */
  M decide(S state, List<M> possible);
}
