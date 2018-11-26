package stratgame.tictactoe;

import java.util.List;
import stratgame.game.State;

/**
 * {@link TTTPlayer} that chooses a move from {@code s.validMoves()} at random.
 */
public class RandomPlayer implements TTTPlayer {

  @Override
  public Integer decide(State<Integer> s) {
    return s.validMoves().get((int) (Math.random() * s.validMoves().size()));
  }
}
