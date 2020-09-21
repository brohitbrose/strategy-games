package stratgame.ai;

import java.util.List;
import stratgame.game.Puzzle;

public abstract class Backtracker<M, P extends Puzzle<M>> implements PuzzleSolver<M, P> {

  private List<M> solve(P puzzle, MoveSuggester<M, P> suggester) {
    while (suggester.hasNext()) {
      final M move = suggester.next();
      final P copy = (P) puzzle.clone();
      copy.makeMove(move);
      if (copy.isSolved()) {
        return copy.moves();
      }
      final List<M> child = solve(copy, moveSuggester(copy));
      if (child != null) {
        return child;
      }
    }
    return null;
  }

  @Override
  public List<M> solve(P puzzle) {
    return solve(puzzle, moveSuggester(puzzle));
  }

  public interface MoveSuggester<M, P extends Puzzle<M>> {

    boolean hasNext();

    M next();

    void seed(P puzzle);

  }

}
