package game.niya;

import game.State;
import java.util.List;

/**
 * {@link NiyaPlayer} that chooses a move from {@code possible} at random.
 */
public final class RandomPlayer implements NiyaPlayer {

  @Override
  public NiyaMove decide(State<NiyaMove> s, List<NiyaMove> possible) {
    return possible.get((int) (Math.random() * possible.size()));
  }
}
