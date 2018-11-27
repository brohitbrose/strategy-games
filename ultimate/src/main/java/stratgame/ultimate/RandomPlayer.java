package stratgame.ultimate;

import stratgame.game.State;

/**
 * {@link UltimatePlayer} that chooses a move from {@code s.validMoves()} at
 * random.
 */
public class RandomPlayer implements UltimatePlayer {

  @Override
  public Integer decide(State<Integer> s) {
    return s.validMoves().get((int) (Math.random() * s.validMoves().size()));
  }
}
