package game.tictactoe;

import game.State;
import java.util.List;

public class RandomPlayer implements TTTPlayer {

  @Override
  public Integer decide(State<Integer> state, List<Integer> possible) {
    return possible.get((int) (Math.random() * possible.size()));
  }
}
