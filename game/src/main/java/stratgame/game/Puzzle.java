package stratgame.game;

import java.util.List;

/**
 * Single-player puzzle-type game.
 *
 * @param <M> the type of move that can be submitted to this {@code Puzzle}.
 */
public interface Puzzle<M> {

  boolean makeMove(M move);

  M lastMove();

  boolean isSolved();

  Puzzle<M> clone();

  List<M> moves();

  void debug();
}
