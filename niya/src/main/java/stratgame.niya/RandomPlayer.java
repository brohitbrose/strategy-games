package stratgame.niya;

import java.util.List;
import stratgame.game.State;

/**
 * {@link NiyaPlayer} that chooses a move from {@code possible} at random.
 */
public final class RandomPlayer implements NiyaPlayer {

  @Override
  public NiyaMove decide(State<NiyaMove> s, List<NiyaMove> possible) {
    return possible.get((int) (Math.random() * possible.size()));
  }
}
