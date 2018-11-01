package game.niya;

import java.util.List;

/**
 * {@link NiyaPlayer} that chooses a move from {@code possible} at random.
 */
public final class RandomPlayer implements NiyaPlayer {

  @Override
  public NiyaMove decide(NiyaState s, List<NiyaMove> possible) {
    return possible.get((int) (Math.random() * possible.size()));
  }
}
