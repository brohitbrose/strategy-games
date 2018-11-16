package game.tictactoe;

import game.Game;
import game.Player;
import game.State;

/**
 * {@link Game} that manages the lifecycle of a Tic-tac-toe match.
 */
public final class TicTacToe implements Game<Integer> {

  /**
   * The {@code Player} that moves on the first and every alternate turn.
   */
  private final TTTPlayer x;

  /**
   * The {@code Player} that moves on the second and every alternate turn.
   */
  private final TTTPlayer o;

  /**
   * Internal {@link TTTState}.
   */
  private final TTTState state;

  /**
   * Initializes a blank {@code TicTacToe} instance.
   */
  public TicTacToe(TTTPlayer x, TTTPlayer o) {
    this.x = x;
    this.o = o;
    this.state = new TTTState();
  }

  @Override
  public Player<Integer> current() {
    return (state.movesMade() & 1) == 0 ? x : o;
  }

  @Override
  public State<Integer> state() {
    return state;
  }

  @Override
  public State<Integer> snapshot() {
    return new TTTState(this.state);
  }

  @Override
  public void start() {
    await();
  }
}
