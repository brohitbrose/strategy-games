package stratgame.game;

import java.util.List;

/**
 * Mutable context used to manage the state of some {@link Game}.
 *
 * @param <M> the type of move that can be submitted to this {@code State}.
 */
public interface State<M> {

  /**
   * The number of moves that have been played so far.
   */
  int movesMade();

  /**
   * The list of {@code M} that can be played in the current turn.
   */
  List<M> validMoves();

  /**
   * If {@code m} is a valid move, plays {@code m} and returns {@code true},
   * otherwise returns {@code false}.
   */
  boolean makeMove(M m);

  /**
   * Returns true if and only if either no moves can be played or a player has
   * won.
   */
  boolean isOver();

  /**
   * Displays a human-readable representation of this {@code State} to {@code
   * stdout}.
   */
  void debug();

  State<M> clone();
}
