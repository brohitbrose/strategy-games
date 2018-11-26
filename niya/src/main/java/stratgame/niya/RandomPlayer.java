package stratgame.niya;

import java.util.List;
import stratgame.game.State;

/**
 * {@link NiyaPlayer} that chooses a move from {@code s.validMoves()} at random.
 */
public final class RandomPlayer implements NiyaPlayer {

  @Override
  public NiyaMove decide(State<NiyaMove> s) {
    return s.validMoves().get((int) (Math.random() * s.validMoves().size()));
  }
}
