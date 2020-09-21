package stratgame.ai;

import java.util.List;
import stratgame.game.Puzzle;

public interface PuzzleSolver<M, P extends Puzzle<M>> {

  List<M> solve(P puzzle);

  Backtracker.MoveSuggester<M, P> moveSuggester(P puzzle);

  default void preprocess() {
    // nop
  }

}
