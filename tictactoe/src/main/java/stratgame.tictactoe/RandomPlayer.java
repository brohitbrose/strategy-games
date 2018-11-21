package stratgame.tictactoe;

import java.util.List;
import stratgame.game.State;

/**
 * {@link TTTPlayer} that chooses a move from {@code possible} at random.
 */
public class RandomPlayer implements TTTPlayer {

  @Override
  public Integer decide(State<Integer> state, List<Integer> possible) {
    return possible.get((int) (Math.random() * possible.size()));
  }
}
