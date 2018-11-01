package game;

import java.util.List;

/**
 * Context used to manage the state of some {@link Game}.
 */
public interface State<T> {

  int movesMade();

  /**
   * The list of {@code T} that can be played in the current turn.
   */
  List<T> validMoves();

  /**
   * If {@code m} is a valid move, plays {@code m} and returns {@code true},
   * otherwise returns {@code false}.
   */
  boolean makeMove(T m);

  /**
   * Returns true if and only if either no moves can be played or a player has
   * won.
   */
  boolean isOver();

  void debug();
}
