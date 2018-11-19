package stratgame.tictactoe;

import stratgame.game.State;
import java.util.List;

/**
 * {@link TTTPlayer} that chooses a move from {@code possible} at random.
 */
public class RandomPlayer implements TTTPlayer {

  @Override
  public Integer decide(State<Integer> state, List<Integer> possible) {
    return possible.get((int) (Math.random() * possible.size()));
  }
}
